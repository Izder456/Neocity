;; Define Namespace & alias for easy use (bleh, boilerplate...)
(ns srcerizder-site.web
  (:require [ring.middleware.content-type :refer [wrap-content-type]]
            [optimus.assets :as assets]
            [optimus.export]
            [optimus.optimizations :as optimizations]
            [optimus.prime :as optimus]
            [optimus.strategies :refer [serve-live-assets]]
            [clojure.java.io :as io]
            [stasis.core :as stasis]
            [srcerizder-site.global :as global]
            [srcerizder-site.os :as os]
            [srcerizder-site.convert :as convert]))

(defn- ensure-dir
  "Ensure Dir is there, otherwise make it!"
  [path]
  (let [dir (io/file path)]
    (when-not (.exists dir)
      (.mkdirs dir))))

(defn- final-render []
  (ensure-dir @global/publics)
  (ensure-dir @global/public-styles)
  (convert/all-to-html @global/edn-docs)
  (convert/all-to-css @global/edn-styles))

(defn- get-pages []
  (stasis/merge-page-sources
   {:public (stasis/slurp-directory "resources/public" #".\.(html|css|png|gif|ico|webmanifest)$")}))

(defn- get-assets []
  (assets/load-assets "public" [#"/styles/." #"/img/.*\.(PNG|GIF|JPG|JPEG|BMP)" #"/blinkies/.*\.(png|gif)"]))

(defn- delete-safe [file-path]
  (if (.exists (io/file file-path))
    (try
      (io/delete-file file-path)
      (catch Exception e (str "Exception caught: " (.getMessage e))))
    false))

(defn- delete-dir [dir-path]
  (let [dir-contents (file-seq (io/file dir-path))
        del-files (filter #(.isFile %) dir-contents)]
    (doseq [file del-files]
      (delete-safe (.getPath file)))
    (delete-safe dir-path)))

(defn- clean []
  (let [dirs [@global/export-dir @global/export-style-dir (str @global/publics [#"\.html$"]) @global/public-styles]]
    (doseq [dir dirs]
      (delete-dir dir))))

(defn- export []
  (clean)
  (final-render)
  (ensure-dir @global/export-dir)
  (ensure-dir @global/export-style-dir)
  (let [assets (optimizations/all (get-assets) {})]
    (stasis/empty-directory! @global/export-dir)
    (optimus.export/save-assets assets @global/export-dir)
    (stasis/export-pages (get-pages) @global/export-dir {:optimus-assets assets})))

(def app (-> (stasis/serve-pages get-pages)
             (optimus/wrap get-assets optimizations/all serve-live-assets)
             wrap-content-type))
