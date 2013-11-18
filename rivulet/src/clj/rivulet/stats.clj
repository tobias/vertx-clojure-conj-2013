(ns rivulet.stats
  (:require [vertx.core :as vertx]
            [vertx.eventbus :as eb]))

(comment
  ;; this is the version that was used during the talk, but requires redis
  (defn init [cfg]
    (vertx/deploy-module "io.vertx~mod-redis~1.1.2")
    (eb/on-message (:result-address cfg)
                   (fn [[filter _]]
                     (eb/send "io.vertx.mod-redis"
                              {:command "incr" :args [filter]}
                              (fn [{:keys [status value message]
                                   :as response}]
                                (if (= "ok" status)
                                  (eb/publish (:stats-address cfg) [filter value])
                                  (throw (ex-info message response)))))))))

;; version that doesn't require redis
(let [counters (atom {})]

  (defn init [cfg]
    (eb/on-message (:result-address cfg)
                   (fn [[filter _]]
                     (swap! counters
                            #(assoc % filter (inc (% filter 0))))
                     (eb/publish (:stats-address cfg)
                                 [filter (@counters filter)])))))

(comment
  
  (defn handle-redis-response [cfg filter
                               {:keys [status value message]
                                :as response}]
    (if (= "ok" status)
      (eb/publish (:stats-address cfg) [filter value])
      (throw (ex-info message response))))
  
  (defn update-match-count [cfg [filter _]]
    (eb/send "io.vertx.mod-redis"
             {:command "incr" :args [filter]}
             (partial handle-redis-response cfg filter)))

  (defn init [cfg]
    (vertx/deploy-module "io.vertx~mod-redis~1.1.2")
    (eb/on-message (:result-address cfg)
                   (partial update-match-count cfg))))


