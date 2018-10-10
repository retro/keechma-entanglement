(ns component-controllers.controllers
  (:require [component-controllers.controllers.counter :as counter]))

(def controllers
  (-> {:counter counter/controller}))
