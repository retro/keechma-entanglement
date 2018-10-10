(ns component-controllers.ui
  (:require [component-controllers.ui.main :as main]
            [component-controllers.ui.article :as article]))

(def ui
  {:main main/component
   :article article/component})
