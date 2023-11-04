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
            [clj-org.org :as cljorg]
            [stasis.core :as stasis]
            [srcerizder-site.os :as os]))

;; Public Dirs
(def publics "resources/public/")
(def public-styles "resources/public/styles/")

;; Private Data
(def edn-docs "resources/private/hiccup")
(def edn-styles "resources/private/garden")
(def org-docs "README.org")

;; Export Dirs
(def export-dir "./dist")
(def export-style-dir "./dist/styles")

(defn load-edn [filename]
  (edn/read-string (slurp filename)))

(def header (list [:head [:meta {:charset "utf-8"}]
                   [:meta {:content "width=device-width, initial-scale=1.0", :name "viewport"}]
                   [:title "Srcerizder!"]
                   [:link {:href "/styles/main.css", :rel "stylesheet"}]]
                  [:link
                   {:rel "apple-touch-icon",
                    :sizes "180x180",
                    :href "/apple-touch-icon.png"}]
                  [:link
                   {:rel "icon",
                    :type "image/png",
                    :sizes "32x32",
                    :href "/favicon-32x32.png"}]
                  [:link
                   {:rel "icon",
                    :type "image/png",
                    :sizes "16x16",
                    :href "/favicon-16x16.png"}]
                  [:link {:rel "manifest", :href "/site.webmanifest"}]))

(def footer (list [:footer
                   [:span
                    [:p
                     [:img {:src "/img/ARROWGRUV.GIF"}]
                     [:br]
                     [:b [:a {:href "#top"} "back to " [:code "$(head -n1)"] " ?"]]
                     [:br]
                     [:b [:a {:href "./index.html"} "back to " [:code "$home"] " ?"]]
                     [:br]
                     [:b [:a {:href "./README.html"} "secret page!"]] [:br]
                     [:b [:a {:href "./index.html"} "index page!"]] [:br]
                     [:br]
                     [:img {:src "/img/ARROWGRUV.GIF"}]
                     [:br]]]]
                  [:center
                   [:h3 "GOOBYE!!!!1!!1"]
                   [:img {:src "/img/GOOBYEGRUV.GIF"}]]))

(defn convert-org-to-edn [org-filename]
  (let [base-filename (-> org-filename
                          (string/replace #"\.org$" ""))
        edn-filename (os/path-append edn-docs (str base-filename ".edn"))
        org-data (cljorg/parse-org (slurp org-filename))
        edn (list (merge header (:content org-data) footer))]
    (spit edn-filename edn)))

(defn convert-to-html [edn-filename]
  (let [base-filename (-> edn-filename
                          (os/basename)
                          (string/replace #"\.edn$" ""))
        html-filename (os/path-append publics (str base-filename ".html"))
        hiccup-data (load-edn edn-filename)
        html (str (hiccup2/html hiccup-data))]
    (spit html-filename html)))

(defn convert-to-css [edn-stylename]
  (let [base-filename (-> edn-stylename
                          (os/basename)
                          (string/replace #"\.edn$" ""))
        css-filename (os/path-append public-styles (str base-filename ".css"))
        garden-data (load-edn edn-stylename)
        css (str (garden/css garden-data))]
    (spit css-filename css)))

(defn convert-all-to-html [edn-directory]
  (convert-org-to-edn org-docs)
  (let [edn-files (file-seq (io/file edn-directory))]
    (doseq [edn-file edn-files
            :when (string/ends-with? edn-file ".edn")]
      (convert-to-html edn-file))))

(defn convert-all-to-css [edn-directory]
  (let [edn-styles (file-seq (io/file edn-directory))]
    (doseq [edn-style edn-styles
            :when (string/ends-with? edn-style ".edn")]
      (convert-to-css edn-style))))

;; Ensure Dir is there, otherwise make it!
(defn ensure-dir [path]
  (let [dir (io/file path)]
    (when-not (.exists dir)
      (.mkdirs dir))))

(defn final-render []
  (ensure-dir publics)
  (ensure-dir public-styles)
  (convert-all-to-html edn-docs)
  (convert-all-to-css edn-styles))

(defn get-pages []
  (stasis/merge-page-sources
   {:public (stasis/slurp-directory "resources/public" #".\.(html|css|png|ico|webmanifest)$")}))

(defn get-assets []
  (assets/load-assets "public" [#"/styles/." #"/img/.*\.(PNG|GIF|JPG|JPEG|BMP)"]))

(defn delete-safe [file-path]
  (if (.exists (io/file file-path))
    (try
      (io/delete-file file-path)
      (catch Exception e (str "Exception caught: " (.getMessage e))))
    false))

(defn delete-dir [dir-path]
  (let [dir-contents (file-seq (io/file dir-path))
        del-files (filter #(.isFile %) dir-contents)]
    (doseq [file del-files]
      (delete-safe (.getPath file)))
    (delete-safe dir-path)))

(defn clean [];
  (delete-dir export-dir)
  (delete-dir export-style-dir)
  (delete-dir (str publics [#"\.html$"]))
  (delete-dir public-styles))

(defn export []
  (clean)
  (final-render)
  (ensure-dir export-dir)
  (ensure-dir export-style-dir)
  (let [assets (optimizations/all (get-assets) {})]
    (stasis/empty-directory! export-dir)
    (optimus.export/save-assets assets export-dir)
    (stasis/export-pages (get-pages) export-dir {:optimus-assets assets})))

(def app (-> (stasis/serve-pages get-pages)
             (optimus/wrap get-assets optimizations/all serve-live-assets)
             wrap-content-type))
