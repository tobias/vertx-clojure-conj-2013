(ns rivulet.stats
  (:require [vertx.core :as vertx]
            [vertx.eventbus :as eb]))

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
                                (throw (ex-info message response))))))))

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


