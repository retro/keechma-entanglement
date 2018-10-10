(ns component-controllers.subscriptions
  (:require [keechma.toolbox.dataloader.subscriptions :as dataloader]
            [component-controllers.edb :refer [edb-schema]]
            [component-controllers.datasources  :refer [datasources]])
  (:require-macros [reagent.ratom :refer [reaction]]))

(defn get-kv [key]
  (fn [app-db-atom]
    (reaction
     (get-in @app-db-atom (flatten [:kv key])))))

(def subscriptions
  {:counter (get-kv :counter)
   :recs (get-kv :recs)})
