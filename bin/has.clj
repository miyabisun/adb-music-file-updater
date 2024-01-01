#!/usr/bin/env bb
(require '[babashka.process :refer [shell]]
         '[clj-yaml.core :as yaml]
         '[clojure.string :as string])

(defn local-find [dir]
  (-> (shell {:out :string} "find" dir "-type" "d")
      :out
      (string/split #"\n")))

(defn ->has-list [dir]
  (let [cnt (-> (string/split dir #"\/") count (+ 2))]
    (->> (local-find dir)
         (map #(string/split % #"\/"))
         (filter #(>= (count %) cnt))
         (map (fn [paths]
                (let [[artist album] (take-last 2 paths)]
                  {:artist artist, :album album})))
         (sort-by (juxt :artist :album)))))

(let [{:keys [dir]} (-> "settings.yml" slurp yaml/parse-string)
      {:keys [source]} dir]
  (->> (->has-list source)
       yaml/generate-string
       print))
