(ns play-clj.utils
  (:require [clojure.string :as s])
  (:import [com.badlogic.gdx.utils Array]))

(def ^:const gdx-package "com.badlogic.gdx.")

(defn- split-key
  [key]
  (-> key name (s/split #"-")))

(defn- join-keys
  [keys]
  (->> keys (map name) (s/join ".") (str gdx-package)))

(defn gdx-static-field*
  [args]
  (->> (last args)
       split-key
       (map s/upper-case)
       (s/join "_")
       (str (join-keys (butlast args)) "/")
       symbol))

(defmacro gdx-static-field
  [& args]
  `~(gdx-static-field* args))

(defn gdx-into-array
  [a]
  (Array. true (into-array a) 1 (count a)))

(defn key->class
  [k]
  (->> (split-key k)
       (map s/capitalize)
       (s/join "")
       symbol))

(defn key->method
  [k]
  (let [parts (split-key k)]
    (->> (rest parts)
         (map s/capitalize)
         (cons (first parts))
         (s/join "")
         (str ".")
         symbol)))

(defmacro call!
  [obj k & args]
  `(~(key->method k) ~obj ~@args))

(defn calls!*
  [[k v]]
  (flatten (list (key->method k) v)))

(defmacro calls!
  [obj & {:keys [] :as args}]
  `(doto ~obj ~@(map calls!* args)))
