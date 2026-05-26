(ns htmx-app.services.user-test
  (:require [clojure.test :refer [deftest is use-fixtures]]
            [next.jdbc :as jdbc]
            [htmx-app.services.user :as user-service]))

(def test-db-url
  (or (System/getenv "TEST_DATABASE_URL")
      "jdbc:postgresql://localhost:5433/htmx_app_test?user=htmx_app_test&password=test_secret"))

(def ds (delay (jdbc/get-datasource test-db-url)))

(use-fixtures :each
  (fn [f]
    (jdbc/execute! @ds ["DELETE FROM users WHERE email = 'test@example.com'"])
    (f)))

(deftest test-create-and-find-user
  (let [user (user-service/create-user! @ds {:email    "test@example.com"
                                              :password "securepassword"
                                              :role     "user"})]
    (is (= "test@example.com" (:users/email user)))
    (is (user-service/verify-password user "securepassword"))
    (is (not (user-service/verify-password user "wrongpassword")))))
