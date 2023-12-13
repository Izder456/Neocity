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
(def org-docs "README.org")

;; Export Dirs
(def export-dir "./dist")
(def export-style-dir "./dist/styles")

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

(defn org-to-edn [org-filename]
  (let [base-filename (-> org-filename
                          (string/replace #"\.org$" ""))
        edn-filename (os/path-append edn-docs (str base-filename ".edn"))
        org-data (cljorg/parse-org (slurp org-filename))
        edn (list (merge header (:content org-data) footer))]
    (spit edn-filename edn)))

(defn hiccup-to-html [edn-filename]
  (let [base-filename (-> edn-filename
                          (os/basename)
                          (string/replace #"\.edn$" ""))
        html-filename (os/path-append publics (str base-filename ".html"))
        hiccup-data (load-edn edn-filename)
        html (str (hiccup2/html hiccup-data))]
    (spit html-filename html)))

(defn garden-to-css [edn-stylename]
  (let [base-filename (-> edn-stylename
                          (os/basename)
                          (string/replace #"\.edn$" ""))
        css-filename (os/path-append public-styles (str base-filename ".css"))
        garden-data (load-edn edn-stylename)
        css (str (garden/css garden-data))]
    (spit css-filename css)))

(defn all-to-html [edn-directory]
  (org-to-edn org-docs)
  (let [edn-files (file-seq (io/file edn-directory))]
    (doseq [edn-file edn-files
            :when (string/ends-with? edn-file ".edn")]
      (hiccup-to-html edn-file))))

(defn all-to-css [edn-directory]
  (let [edn-styles (file-seq (io/file edn-directory))]
    (doseq [edn-style edn-styles
            :when (string/ends-with? edn-style ".edn")]
      (garden-to-css edn-style))))
