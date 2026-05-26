(ns htmx-app.services.user
  (:require [htmx-app.repository.user :as repo]
            [htmx-app.schemas         :as schemas]
            [htmx-app.validation      :as validation]
            [buddy.hashers            :as hashers]))

(defn validate-registration [params]
  (validation/first-error schemas/registration-schema params))

(defn find-by-email [ds email]
  (repo/find-by-email ds email))

(defn find-all [ds]
  (repo/find-all ds))

(defn verify-password [user raw-password]
  (hashers/check raw-password (:users/password user)))

(defn create! [ds {:keys [email password role]}]
  (repo/create! ds {:email    email
                    :password (hashers/derive password)
                    :role     (or role "user")}))
