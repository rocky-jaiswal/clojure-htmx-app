(ns htmx-app.commands.auth
  (:require [htmx-app.services.user  :as user-service]
            [htmx-app.commands.pipeline :as pipe]
            [clojure.string          :as str]))

(defn- validate-registration [{:keys [email password confirm-password]}]
  (cond
    (str/blank? email)               "Email is required"
    (> (count email) 254)            "Email must be 254 characters or less"
    (not (str/includes? email "@"))  "Email must contain @"
    (str/blank? password)            "Password is required"
    (< (count password) 8)           "Password must be at least 8 characters"
    (> (count password) 72)          "Password must be 72 characters or less"
    (not (re-find #"\d" password))   "Password must contain at least one number"
    (not (re-find #"[^a-zA-Z0-9]" password)) "Password must contain at least one special character"
    (not= password confirm-password) "Passwords do not match"))

(defn register! [ds {:keys [email password] :as params}]
  (-> {:ok params}
      (pipe/valid #(validate-registration params))
      (pipe/valid #(when (user-service/find-by-email ds email) "Email already registered"))
      (pipe/then  (fn [_]
                    (let [user (user-service/create! ds {:email email :password password})]
                      {:ok {:id    (:users/id user)
                            :email (:users/email user)
                            :role  (:users/role user)}})))))

(defn login [ds {:keys [email password]}]
  (let [user (user-service/find-by-email ds email)]
    (if (and user (user-service/verify-password user password))
      {:ok {:id    (:users/id user)
            :email (:users/email user)
            :role  (:users/role user)}}
      {:error "Invalid email or password"})))
