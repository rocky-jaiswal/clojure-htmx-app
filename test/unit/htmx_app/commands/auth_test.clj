(ns htmx-app.commands.auth-test
  (:require [clojure.test           :refer [deftest is testing]]
            [buddy.hashers          :as hashers]
            [htmx-app.commands.auth :as cmd]
            [htmx-app.services.user :as user-svc]))

(def ^:private existing-user
  {:users/id 1 :users/email "taken@example.com" :users/role "user"
   :users/password (hashers/derive "correct-pw")})

(def ^:private valid-password "Pass1234!")

(deftest register!
  (testing "blank email"
    (is (= "Email is required"
           (:error (cmd/register! nil {:email ""
                                       :password valid-password
                                       :confirm-password valid-password})))))

  (testing "email missing @"
    (is (= "Email must contain @"
           (:error (cmd/register! nil {:email "notanemail"
                                       :password valid-password
                                       :confirm-password valid-password})))))

  (testing "password too short"
    (is (= "Password must be at least 8 characters"
           (:error (cmd/register! nil {:email "a@b.com"
                                       :password "sh0rt!"
                                       :confirm-password "sh0rt!"})))))

  (testing "password missing number"
    (is (= "Password must contain at least one number"
           (:error (cmd/register! nil {:email "a@b.com"
                                       :password "Password!"
                                       :confirm-password "Password!"})))))

  (testing "password missing special character"
    (is (= "Password must contain at least one special character"
           (:error (cmd/register! nil {:email "a@b.com"
                                       :password "Password1"
                                       :confirm-password "Password1"})))))

  (testing "mismatched passwords"
    (is (= "Passwords do not match"
           (:error (cmd/register! nil {:email "a@b.com"
                                       :password valid-password
                                       :confirm-password "different"})))))

  (testing "email already taken"
    (with-redefs [user-svc/find-by-email (fn [_ _] existing-user)]
      (is (= "Email already registered"
             (:error (cmd/register! nil {:email "taken@example.com"
                                         :password valid-password
                                         :confirm-password valid-password}))))))

  (testing "successful registration returns normalized user"
    (with-redefs [user-svc/find-by-email (fn [_ _] nil)
                  user-svc/create!       (fn [_ _] {:users/id 2
                                                    :users/email "new@example.com"
                                                    :users/role "user"})]
      (let [result (cmd/register! nil {:email "new@example.com"
                                       :password valid-password
                                       :confirm-password valid-password})]
        (is (= 2 (get-in result [:ok :id])))
        (is (= "new@example.com" (get-in result [:ok :email])))))))

(deftest login
  (testing "wrong password"
    (with-redefs [user-svc/find-by-email  (fn [_ _] existing-user)
                  user-svc/verify-password (fn [_ _] false)]
      (is (= "Invalid email or password"
             (:error (cmd/login nil {:email "taken@example.com" :password "wrong"}))))))

  (testing "unknown email"
    (with-redefs [user-svc/find-by-email (fn [_ _] nil)]
      (is (= "Invalid email or password"
             (:error (cmd/login nil {:email "nobody@example.com" :password "pass"}))))))

  (testing "successful login returns normalized user"
    (with-redefs [user-svc/find-by-email  (fn [_ _] existing-user)
                  user-svc/verify-password (fn [_ _] true)]
      (let [result (cmd/login nil {:email "taken@example.com" :password "correct-pw"})]
        (is (= 1 (get-in result [:ok :id])))
        (is (= "taken@example.com" (get-in result [:ok :email])))))))
