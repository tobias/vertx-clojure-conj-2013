(ns rivulet.filter
  (:require [vertx.eventbus :as eb]))

(defn- match-filter [result-address filter data]
  (when (re-find (re-pattern filter) data)
    (eb/publish result-address [filter data])))

(defn- delete-filter [state filter]
  (when-let [handler-id (@state filter)]
    (eb/unregister-handler handler-id)
    (swap! state dissoc filter)))

(defn- add-filter [state stream-address result-address filter]
  (when-not (@state filter)
    (swap!
     state assoc filter
     (eb/on-message stream-address
                    (partial match-filter result-address filter)))))

(defn- dispatch [state stream-address result-address
                 {:keys [command payload]}]
  (case command
    "add-filter"    (add-filter state stream-address
                                result-address payload)
    "delete-filter" (delete-filter state payload)
    :do-nothing))

(defn init [{:keys [command-address stream-address result-address]}]
  (let [state (atom {})]
    (eb/on-message
     command-address
     (partial dispatch state stream-address result-address))))

