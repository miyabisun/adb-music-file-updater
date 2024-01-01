#!/usr/bin/env bb
(require '[babashka.process :refer [shell]]
         '[clj-yaml.core :as yaml])

(defn push [source dist]
  (shell "adb" "shell" "mkdir" "-p" dist)
  (shell "adb" "push" (str source "/.") dist))

(let [{:keys [dir]} (-> "settings.yml" slurp yaml/parse-string)
      {:keys [source dist]} dir]
  (push source dist)
  (println "finished"))
