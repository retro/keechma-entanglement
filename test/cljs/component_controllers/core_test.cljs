(ns component-controllers.core-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [component-controllers.core :as core]))

(deftest fake-test
  (testing "fake description"
    (is (= 1 2))))
