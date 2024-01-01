#!/usr/bin/env bb
(require '[babashka.process :refer [shell]]
         '[clj-yaml.core :as yaml]
         '[clojure.string :as string])

(defn adb-find [dir]
  (-> (shell {:out :string} "adb" "shell" "find" (str "\"" dir "\"") "-type" "d")
      :out
      (string/split #"\n")))

(defn ->now-list [dir]
  (let [cnt (-> (string/split dir #"\/") count (+ 2))]
    (->> (adb-find dir)
         (map #(string/split % #"\/"))
         (filter #(>= (count %) cnt))
         (map (fn [paths]
                (let [[artist album] (take-last 2 paths)]
                  {:artist artist, :album album})))
         (sort-by (juxt :artist :album)))))

(let [{:keys [dir]} (-> "settings.yml" slurp yaml/parse-string)
      {:keys [dist]} dir]
  (-> (->now-list dist)
      yaml/generate-string
      print))
