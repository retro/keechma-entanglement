(ns component-controllers.controllers.counter
  (:require [keechma.toolbox.pipeline.core :as pp :refer-macros [pipeline!]]
            [keechma.toolbox.pipeline.controller :as pp-controller]))

(defn update-counter-value [action app-db]
  (let [current (or (get-in app-db [:kv :counter]) 0)
        action-fn (if (= action :inc) inc dec)]
    (assoc-in app-db [:kv :counter] (action-fn current))))

(def controller
  (pp-controller/constructor
   (fn [_] true)
   {:on-start (pipeline! [value app-db]
             (pp/commit! (assoc-in app-db [:kv :counter] 0))
             (pp/execute! :add-article nil))
    :update (pipeline! [value app-db]
              (pp/commit! (update-counter-value value app-db)))
    :add-article (pipeline! [value app-db]
                   (pp/broadcast! :foo :bar)
                   (pp/commit! (update-in app-db [:kv :recs] #(vec (conj % (str (gensym "rec")))))))}))
