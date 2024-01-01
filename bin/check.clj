#!/usr/bin/env bb
(require '[babashka.process :refer [shell]]
         '[babashka.fs :as fs]
         '[clj-yaml.core :as yaml])

(defn ->albums [source artist]
  (->> (str source "/" artist)
       fs/list-dir
       (filter fs/directory?)
       (map fs/file-name)))

(defn ->artists [source]
  (->> source
       fs/list-dir
       (filter fs/directory?)
       (map fs/file-name)))

(let [{:keys [dir]} (-> "settings.yml" slurp yaml/parse-string)
      {:keys [source dist]} dir]
  (println "=== local ===")
  (println "")
  (println "=> du -hs" source)
  (shell "du" "-hs" source)
  (println "")

  (println "=== android ===")
  (println "")
  (println "=> df -h" dist)
  (shell "adb" "shell" "df" "-h" dist)
  (println "")
  (println "=> du -hs" dist)
  (shell "adb" "shell" "du" "-hs" dist))
