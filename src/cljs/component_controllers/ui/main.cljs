(ns component-controllers.ui.main
  (:require [keechma.ui-component :as ui]
            [keechma.toolbox.ui :refer [sub> <cmd]]))

(defn render [ctx]
  (let [counter (sub> ctx :counter)]
    [:div
     [:button {:on-click #(<cmd ctx :update :dec)} "Decrement"]
     [:button {:on-click #(<cmd ctx :update :inc)} "Increment"]
     [:button {:on-click #(<cmd ctx :add-article nil) } "Add rec"]
     [:p (str "Count: " counter)]
     [:hr]
     [(ui/component ctx :article) counter]
     [:hr]
     [(ui/component ctx :article) counter]]))

(def component (ui/constructor {:renderer render
                                :topic :counter
                                :component-deps [:article]
                                :subscription-deps [:counter]}))
