(ns component-controllers.ui.article
  (:require [keechma.ui-component :as ui]
            [keechma.toolbox.ui :refer [sub> <cmd]]
            [keechma.toolbox.entangled.ui :as entangled-ui :refer [<comp-cmd <comp-swap <comp-reset]]
            [keechma.toolbox.pipeline.core :as pp :refer-macros [pipeline!]]
            [keechma.toolbox.entangled.pipeline :as epp]))

(defn state-provider [ctx local-state [counter]]
  {:recs (sub> ctx :recs)
   :counter counter
   :article (:id local-state)
   :testing (:testing local-state)})

(defn render [ctx state]
  [:div
   [:h1 "-> ARTICLE! #" (:counter state) " - " (:article state) " > " (:testing state)]
   (into [:ul] (map (fn [r] [:li r]) (:recs state)))
   [:button {:on-click #(<comp-cmd ctx :something-custom {:SOMETHING :CUSTOM})} "TEST"]
   [:button {:on-click #(<comp-swap ctx assoc-in [:testing] "THIS IS NEW TESTING")} "THIS IS NEW"]
   [:button {:on-click #(<comp-reset ctx {:id "lala" :testing "bebe"})} "RESET"]])


(def component 
  (entangled-ui/constructor
   {:renderer          render
    :state-provider    state-provider
    :subscription-deps [:recs]}
   {:on-init          (pipeline! [value app-db]
                        (epp/comp-commit! {:id (str (gensym :article))})
                        (epp/comp-execute! :something-custom :something-custom-value))
    :on-state-change  (pipeline! [value app-db])
    :something-custom (pipeline! [value app-db ctx]
                        (println "!!!!" value)
                        (epp/comp-commit! (merge (epp/comp-state app-db ctx) {:testing (gensym "YES")})))}))
