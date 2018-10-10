(defproject component-controllers "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.339"]
                 [reagent "0.8.1"]
                 [keechma "0.3.13" :exclusions [cljsjs/react-with-addons cljsjs/react-dom cljsjs/react-dom-server]]
                 [keechma/toolbox "0.1.19" :exclusions [cljsjs/react-with-addons cljsjs/react-dom cljsjs/react-dom-server]]]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj"]

  :plugins [[lein-cljsbuild "1.1.7"]]

  :clean-targets ^{:protect false} ["resources/public/js"
                                    "target"
                                    "test/js"]

  :figwheel {:css-dirs ["resources/public/css"]}


  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}

  :profiles
  {:dev
   {:dependencies [
                   [figwheel-sidecar "0.5.10"]
                   [com.cemerick/piggieback "0.2.1"]
                   [binaryage/devtools "0.8.2"]]

    :plugins      [[lein-figwheel "0.5.16"]
                   [lein-doo "0.1.7"]]}}

  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/cljs"]
     :figwheel     {:on-jsload "component-controllers.core/reload"}
     :compiler     {:main                 component-controllers.core
                    :optimizations        :none
                    :output-to            "resources/public/js/app.js"
                    :output-dir           "resources/public/js/dev"
                    :asset-path           "js/dev"
                    :source-map-timestamp true
                    :preloads             [devtools.preload]
                    :external-config
                    {:devtools/config
                     {:features-to-install    [:formatters :hints]
                      :fn-symbol              "F"
                      :print-config-overrides true}}}}

    {:id           "min"
     :source-paths ["src/cljs"]
     :compiler     {:main            component-controllers.core
                    :optimizations   :advanced
                    :output-to       "resources/public/js/app.js"
                    :output-dir      "resources/public/js/min"
                    :elide-asserts   true
                    :closure-defines {goog.DEBUG false}
                    :pretty-print    false}}

    {:id           "test"
     :source-paths ["src/cljs" "test/cljs"]
     :compiler     {:output-to     "resources/public/js/test.js"
                    :output-dir    "resources/public/js/test"
                    :main          component-controllers.runner
                    :optimizations :none}}
    ]})
