;; Define Namespace & alias for easy use (bleh, boilerplate...)
(ns srcerizder-site.web
  (:require [ring.middleware.content-type :refer [wrap-content-type]]
            [optimus.assets :as assets]
            [optimus.export]
            [optimus.link :as link]
            [optimus.optimizations :as optimizations]
            [optimus.prime :as optimus]
            [optimus.strategies :refer [serve-live-assets]]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [hiccup.core :refer [html]]
            [stasis.core :as stasis]
            [srcerizder-site.page :refer :all]
            [net.cgrand.enlive-html :as enlive]))

;;;;;;;;;;;;;;;;
;; Asset Pull ;;
;;;;;;;;;;;;;;;;

;; Pull down assets for images and styles
(defn get-assets []
  (assets/load-assets "public" [#"/styles/.*"
                                #"/img/.*\.(PNG|GIF|JPG|JPEG|BMP)"]))

;;;;;;;;;;;;;;;;
;; Page Parse ;;
;;;;;;;;;;;;;;;;

(defn hiccup-to-html [hiccup]
  (apply str (map str hiccup)))

(def regular-html (hiccup-to-html (hiccup-html)))

(defn render-pages []
  (stasis/merge-page-sources
   {:public (stasis/slurp-directory "resources/public" #".*\.(html|css|js)$")
    :partials (:regular-html (regular-html))}))

;;;;;;;;;;;;;;;
;; ProdStuff ;;
;;;;;;;;;;;;;;;

;; serve!
(def app (-> (stasis/serve-pages get-pages)
             (optimus/wrap get-assets optimizations/all serve-live-assets)
             wrap-content-type))

;; define export info for rendered static page
(def export-dir "dist")

(defn export []
  (let [assets (optimizations/all (get-assets) {})]
    (stasis/empty-directory! export-dir)
    (optimus.export/save-assets assets export-dir)
    (stasis/export-pages (get-pages) export-dir {:optimus-assets assets})))
