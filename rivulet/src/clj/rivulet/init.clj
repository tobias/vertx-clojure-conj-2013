(ns rivulet.init
  (:require [vertx.repl :as repl]
            [vertx.core :as core]
            [rivulet.config :refer [config]]
            [rivulet.filter :as filter]
            [rivulet.stats :as stats]))

(defn init []
  (filter/init config)
  (stats/init config)
  (core/deploy-verticle "data_source.rb" :config config)
  (core/deploy-verticle "web.js" :config config :instances 2)
  (repl/start 0))
