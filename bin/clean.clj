#!/usr/bin/env bb
(require '[babashka.process :refer [shell]]
         '[clj-yaml.core :as yaml]
         '[clojure.string :as string])

(defn rm [dir]
  (shell "adb" "shell" "rm" "-rfv" (str dir "/*")))

(let [{:keys [dir]} (-> "settings.yml" slurp yaml/parse-string)
      {:keys [dist]} dir]
  (rm dist)
  (println "finished"))
