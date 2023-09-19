;; Define Namespace & alias for easy use (bleh, boilerplate...)
(ns srcerizder-site.web
  (:require [ring.middleware.content-type :refer [wrap-content-type]]
            [optimus.assets :as assets]
            [optimus.export]
            [optimus.optimizations :as optimizations]
            [optimus.prime :as optimus]
            [optimus.strategies :refer [serve-live-assets]]
            [clojure.string :as string]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [hiccup2.core :as hiccup2]
            [stasis.core :as stasis]))

;;;;;;;;;;;;;;;;
;; Asset Pull ;;
;;;;;;;;;;;;;;;;

;; Pull down assets for images and styles
(defn get-assets []
  (assets/load-assets "public" [#"/styles/.*" #"/img/.*\.(PNG|GIF|JPG|JPEG|BMP)"]))

;;;;;;;;;;;;;;;
;; EDN  Docs ;;
;;;;;;;;;;;;;;;

;; Public dir
(def publics "resources/public/")

;; Load in edn docs
(defn load-edn [filename]
  (edn/read-string (slurp filename)))

;; Convert 1 (one) edn doc to html via argument
(defn convert-to-html [edn-filename]
  (let [base-filename (-> edn-filename
                          (.getName)
                          (string/replace #"\.edn$" ""))
        html-filename (str publics base-filename ".html")
        hiccup-data (load-edn edn-filename)
        html (hiccup2/html hiccup-data)]
    (spit html-filename html)))

;; Recurse over a seq of all (any) edn docs and run (convert-to-html) on them
(defn convert-all-to-html [edn-directory]
  (let [edn-files (file-seq (io/file edn-directory))]
    (doseq [edn-file edn-files
            :when (.endsWith
                   (.getName edn-file) ".edn")]
      (convert-to-html edn-file))))

;; Private edn dir
(def edn-docs "resources/private/edn")

;;;;;;;;;;;;;;;;
;; Page Logic ;;
;;;;;;;;;;;;;;;;

(defn get-pages []
  (convert-all-to-html edn-docs)
  (stasis/merge-page-sources
   {:public (stasis/slurp-directory "resources/public" #".*\.(html|css|js)$")}))

;;;;;;;;;;;;;;;
;; ProdStuff ;;
;;;;;;;;;;;;;;;

;; serve!
(def app (-> (stasis/serve-pages get-pages)
             (optimus/wrap get-assets optimizations/all serve-live-assets)
             wrap-content-type))

;; Ensure Dist is there
(defn ensure-dist [path]
  (let [dir (io/file path)]
    (when-not (.exists dir)
      (.mkdirs dir)))))

;; define export location
(def export-dir "dist")

(defn export []
  (ensure-dist export-dir)
  (let [assets (optimizations/all (get-assets) {})]
    (stasis/empty-directory! export-dir)
    (optimus.export/save-assets assets export-dir)
    (stasis/export-pages (get-pages) export-dir {:optimus-assets assets})))
