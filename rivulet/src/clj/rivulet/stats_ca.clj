(ns rivulet.stats-ca
  (:require [vertasync.eventbus :as eb]
            [clojure.core.async :as async]))

(defn handle-redis-response [cfg [filter {:keys [status value message]
                                          :as response}]]
  (if (= "ok" status)
    (eb/publish (:stats-address cfg) [filter value])
    (throw (ex-info message response))))

(defn update-match-count [cfg [filter _]]
  [filter
   (async/<! (eb/send-with-reply-chan "io.vertx.mod-redis"
                                      {:command "incr" :args [filter]}))])

(defn init [cfg]
  (vertx/deploy-module "io.vertx~mod-redis~1.1.2")
  (let [results (eb/message-chan (:result-address cfg))]
    (async/go-loop []
      (when-let [msg (async/<! results)]
        (->> msg
             update-match-count
             (handle-redis-response cfg))
        (recur)))))

(comment
  ;; !!! WARNING: VAPORWARE !!!
)
