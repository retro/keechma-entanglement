(ns component-controllers.core 
  (:require [reagent.core :as reagent]
            [keechma.app-state :as app-state]
            [keechma.toolbox.dataloader.app :as dataloader]
            [keechma.toolbox.forms.app :as forms]
            [component-controllers.controllers :refer [controllers]]
            [component-controllers.ui :refer [ui]]
            [component-controllers.subscriptions :refer [subscriptions]]
            [component-controllers.edb :refer [edb-schema]]
            [component-controllers.datasources :refer [datasources]]
            [component-controllers.forms :as component-controllers-forms]
            [keechma.toolbox.entangled.app :as entangled]))

(def app-definition
  (-> {:components    ui
       :controllers   controllers
       :subscriptions subscriptions
       :html-element  (.getElementById js/document "app")}
      (dataloader/install datasources edb-schema)
      (forms/install component-controllers-forms/forms component-controllers-forms/forms-automount-fns)
      (entangled/install)))

(defonce running-app (clojure.core/atom nil))

(defn start-app! []
  (reset! running-app (app-state/start! app-definition)))

(defn dev-setup []
  (when ^boolean js/goog.DEBUG
    (enable-console-print!)
    (println "dev mode")))

(defn reload []
  (let [current @running-app]
    (if current
      (app-state/stop! current start-app!)
      (start-app!))))

(defn ^:export main []
  (dev-setup)
  (start-app!))
