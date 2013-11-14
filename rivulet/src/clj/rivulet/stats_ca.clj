(ns rivulet.stats-ca
  (:require [vertasync.eventbus :as eb]
            [clojure.core.async :refer [<! >! chan go-loop]]))

(defn handle-redis-response [cfg [filter {:keys [status value message]
                                          :as response}]]
  (if (= "ok" status)
    (eb/publish (:stats-address cfg) [filter value])
    (throw (ex-info message response))))

(defn update-match-count [cfg [filter _]]
  (let [filter-reply-chan (chan)
        reply-chan (eb/send-with-reply-chan "io.vertx.mod-redis"
                                            {:command "incr" :args [filter]})]
    (>! filter-reply-chan [filter (<! reply-chan)])
    filter-reply-chan))

(defmacro <!->>
  "Threading macro that takes from the channel returned by each step,
     threading the result to the next."
  [& args]
  `(->>...))

(defn init [cfg]
  (vertx/deploy-module "io.vertx~mod-redis~1.1.2")
  (go-loop [results (eb/message-chan (:result-address cfg))]
    (<!->> results
           update-match-count
           (handle-redis-response cfg))
    (recur results)))

(comment
  ;; !!! WARNING: VAPORWARE !!!
)
