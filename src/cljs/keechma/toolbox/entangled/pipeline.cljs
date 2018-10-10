(ns keechma.toolbox.entangled.pipeline
  (:require [keechma.toolbox.pipeline.core :as pp]
            [keechma.toolbox.entangled.shared :refer [id ->ComponentCommand]]
            [keechma.controller :as controller]))

(defn comp-state [app-db ctx]
  (get-in app-db (:keechma.toolbox.entangled/app-db-path ctx)))

(defn comp-commit! [data]
  (with-meta
    (fn [{:keys [component-name component-id]} app-db-atom _ _]
      (swap! app-db-atom assoc-in [:kv id component-name component-id] data)
      nil)
    {:pipeline? true}))

(defn comp-execute!
  ([cmd] (comp-execute! cmd nil))
  ([cmd args]
   (with-meta
     (fn [controller app-db-atom _ _]
       (controller/execute controller cmd (->ComponentCommand (:component-id controller) args))
       nil)
     {:pipeline? true})))
