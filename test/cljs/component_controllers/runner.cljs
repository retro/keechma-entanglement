(ns component-controllers.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [component-controllers.core-test]))

(doo-tests 'component-controllers.core-test)
