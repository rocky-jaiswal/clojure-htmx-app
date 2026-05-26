(ns htmx-app.commands.auth
  (:require [htmx-app.services.user     :as user-service]
            [htmx-app.commands.pipeline :as pipe]
            [malli.core                 :as m]))

(defn register! [ds {:keys [email password] :as params}]
  (-> {:ok params}
      (pipe/valid #(user-service/validate-registration params))
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

(def ^:private credentials
  [:map [:email :string] [:password :string]])

(m/=> register! [:=> [:cat :any (conj credentials [:confirm-password :string])] pipe/Result])
(m/=> login     [:=> [:cat :any credentials] pipe/Result])
