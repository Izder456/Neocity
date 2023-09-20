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
            [garden.core :as garden]
            [hiccup2.core :as hiccup2]
            [stasis.core :as stasis]))

;;;;;;;;;;;;;;;
;; EDN  Data ;;
;;;;;;;;;;;;;;;

;; Public dir
(def publics "resources/public/")
(def public-styles "resources/public/styles/")

;; Private edn dirs
(def edn-docs "resources/private/hiccup")
(def edn-styles "resources/private/garden")

;; Load in edn files
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

;; Convert 1 (one) edn doc to html via argument
(defn convert-to-css [edn-stylename]
  (let [base-filename (-> edn-stylename
                          (.getName)
                          (string/replace #"\.edn$" ""))
        css-filename (str public-styles base-filename ".css")
        garden-data (load-edn edn-stylename)
        css (garden/css garden-data)]
    (spit css-filename css)))

;; Recurse over a seq of all (any) edn docs and run (convert-to-css) on them
(defn convert-all-to-css [edn-directory]
  (let [edn-styles (file-seq (io/file edn-directory))]
    (doseq [edn-style edn-styles
            :when (.endsWith
                   (.getName edn-style) ".edn")]
      (convert-to-css edn-style))))

;;;;;;;;;;;;;;;;
;; Ensure dir ;;
;;;;;;;;;;;;;;;;

;; Ensure Dir is there, otherwise make it!
(defn ensure-dir [path]
  (let [dir (io/file path)]
    (when-not (.exists dir)
      (.mkdirs dir))))

;;;;;;;;;;;;;;;;
;; Page Logic ;;
;;;;;;;;;;;;;;;;

;; Render hiccup and garden edn maps
(defn final-render []
  (ensure-dir publics)
  (ensure-dir public-styles)
  (convert-all-to-html edn-docs)
  (convert-all-to-css edn-styles))

;; Get page data ready
(defn get-pages []
  (stasis/merge-page-sources
   {:public (stasis/slurp-directory "resources/public" #".*\.(html|css)$")}))

;;;;;;;;;;;;;;;;
;; Asset Pull ;;
;;;;;;;;;;;;;;;;

;; Pull assets for images and styles
(defn get-assets []
  (assets/load-assets "public" [#"/styles/.*" #"/img/.*\.(PNG|GIF|JPG|JPEG|BMP)"]))

;;;;;;;;;;;;;;;
;; ProdStuff ;;
;;;;;;;;;;;;;;;

;; Safe Delete
(defn delete-safe [file-path]
  (if (.exists (io/file file-path))
    (try
      (io/delete-file file-path)
      (catch Exception e (str "Exception caught: " (.getMessage e))))
    false))

;; Recursive delete
(defn delete-dir [dir-path]
  (let [dir-contents (file-seq (io/file dir-path))
        del-files (filter #(.isFile %) dir-contents)]
    (doseq [file del-files]
      (delete-safe (.getPath file)))
    (delete-safe dir-path)))

;; Define export location
(def export-dir "./dist")
(def export-style-dir "./dist/styles")

;; Clean target
(defn clean []
  (delete-dir export-dir)
  (delete-dir export-style-dir)
  (delete-dir (str publics [#"\.html$"]))
  (delete-dir public-styles))

;; Export Target
(defn export []
  (clean)
  (final-render)
  (ensure-dir export-dir)
  (ensure-dir export-style-dir)
  (let [assets (optimizations/all (get-assets) {})]
    (stasis/empty-directory! export-dir)
    (optimus.export/save-assets assets export-dir)
    (stasis/export-pages (get-pages) export-dir {:optimus-assets assets})))

;; Serve for debugging with ring
(def app (-> (export)
             (stasis/serve-pages get-pages)
             (optimus/wrap get-assets optimizations/all serve-live-assets)
             wrap-content-type))
