(ns keechma.toolbox.entangled.controller
  (:require [keechma.toolbox.pipeline.core :as pp :refer-macros [pipeline!]]
            [keechma.toolbox.pipeline.controller :as pp-controller]
            [keechma.controller :as controller]
            [cljs.core.async :refer [<! put!]]
            [keechma.toolbox.entangled.shared :refer [id ComponentCommand ->ComponentCommand]]
            [promesa.core :as p]
            [medley.core :refer [dissoc-in]]
            [keechma.toolbox.entangled.pipeline :as epp])
  (:require-macros [cljs.core.async.macros :refer [go-loop]]))

(defn swap-comp-state [state args]
  (let [f (first args)
        args (concat [state] (rest args))]
    (println "----" f args)
    (apply f args)))

(def default-pipelines
  {:keechma.toolbox.entangled.actions/reset
   (pipeline! [value app-db]
     (epp/comp-commit! value))
   :keechma.toolbox.entangled.actions/swap
   (pipeline! [value app-db ctx]
     (epp/comp-commit! (swap-comp-state (epp/comp-state app-db ctx) value)))})

(defn get-real-pipeline-name [pipelines pipeline-name]
  (let [pipeline (get pipelines pipeline-name)]
    (if (keyword? pipeline)
      (get-real-pipeline-name pipelines pipeline)
      pipeline-name)))

(defn make-run-pipeline [this app-db-atom in-chan]
  (let [component-name (:component-name this)
        pipelines      (:pipelines this)]
    (fn [command payload pipelines$]
      (let [component-id (:id payload)
            args (:args payload)
            pipeline-name      command
            real-pipeline-name (get-real-pipeline-name pipelines pipeline-name)
            pipeline           (pipelines real-pipeline-name)
            pipeline-name-id   [real-pipeline-name (keyword (gensym component-id))]
            ctrl-with-extras   (-> this
                                   (assoc :pipeline/running pipeline-name-id
                                          :component-id component-id)
                                   (assoc-in [:context :keechma.toolbox.entangled/app-db-path]
                                             [:kv id component-name component-id]))]
        (when pipeline
          (swap! pipelines$ assoc-in pipeline-name-id
                 {:running? true
                  :args     args 
                  :promise  (->> (pipeline ctrl-with-extras app-db-atom args pipelines$)
                                 (p/map (fn [val]
                                          (swap! pipelines$ dissoc-in pipeline-name-id)
                                          val))
                                 (p/error (fn [err]
                                            (swap! pipelines$ dissoc-in pipeline-name-id)
                                            (throw err))))}))))))

(defrecord EntangledController [component-name pipelines])

(defmethod controller/params EntangledController [_ _] true)

(defmethod controller/handler EntangledController [this app-db-atom in-chan _]
  (let [run-pipeline (make-run-pipeline this app-db-atom in-chan)]
    (go-loop [components-state  {}]
      (let [[command args] (<! in-chan)]
        (when command
          (if-not (instance? ComponentCommand args)
            (let [registered-components-ids (keys components-state)]
              (doseq [id registered-components-ids]
                (put! in-chan [command (->ComponentCommand id args)]))
              (recur components-state))
            (case command
              :on-init      (let [pipelines$      (atom {})
                                  component-state {:pipelines$ pipelines$}
                                  id              (:id args)]
                              (run-pipeline command args pipelines$)
                              (recur (assoc components-state id component-state)))

              :on-terminate (let [id         (:id args)
                                  pipelines$ (get-in components-state [:id :pipelines$])]
                              (reset! pipelines$ {})
                              (run-pipeline command args pipelines$)
                              (recur (dissoc components-state id)))
              (let [id (:id args)
                    component-state (components-state id)]
                (when component-state
                  (run-pipeline command args (:pipelines$ component-state)))
                (recur components-state)))))))))

(defn register
  ([component-name pipelines]
   (register {} component-name pipelines))
  ([controllers component-name pipelines]
   (assoc controllers component-name (->EntangledController component-name (merge pipelines default-pipelines)))))

