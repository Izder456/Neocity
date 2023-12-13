(ns srcerizder-site.convert
  (:require
   [garden.core :as garden]
   [hiccup2.core :as hiccup2]
   [clj-org.org :as cljorg]
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [clojure.string :as string]
   [srcerizder-site.os :as os]))

;; Public Dirs
(def publics "resources/public/")
(def public-styles "resources/public/styles/")

;; Private Data
(def edn-docs "resources/private/hiccup")
(def edn-styles "resources/private/garden")

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

(defn load-edn [filename]
  (edn/read-string (slurp filename)))

(defn- load-and-generate-filename
  "Helper function for loading and generating filenames"
  [edn-filename base-dir ext]
  (let [base-filename (-> edn-filename
                          (os/basename)
                          (string/replace #"\.edn$" ""))
        filename (os/path-append base-dir (str base-filename ext))
        edn-data (load-edn edn-filename)]
    [filename edn-data]))

(defn- hiccup-to-html
  "Hiccup EDN data to html conversion"
  [edn-filename]
  (let [[html-filename hiccup-data] (load-and-generate-filename edn-filename publics ".html")]
    (spit html-filename (str (hiccup2/html hiccup-data)))))

(defn- garden-to-css
  "Garden EDN data to css conversion"
  [edn-stylename]
  (let [[css-filename garden-data] (load-and-generate-filename edn-stylename public-styles ".css")]
    (spit css-filename (str (garden/css garden-data)))))

(defn- process-files
  "Helper function for file processing"
  [edn-directory process-fn]
  (let [edn-files (file-seq (io/file edn-directory))]
    (doseq [edn-file edn-files
            :when (string/ends-with? edn-file ".edn")]
      (process-fn edn-file))))

(defn all-to-html [edn-directory]
  (process-files edn-directory hiccup-to-html))

(defn all-to-css [edn-directory]
  (process-files edn-directory garden-to-css))
