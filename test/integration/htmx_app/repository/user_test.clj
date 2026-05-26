(ns htmx-app.repository.user-test
  (:require [clojure.test           :refer [deftest is use-fixtures testing]]
            [htmx-app.test-helpers  :refer [test-ds db-fixture]]
            [htmx-app.repository.user :as repo]))

(use-fixtures :each db-fixture)

(deftest find-by-email-returns-nil-for-unknown-user
  (is (nil? (repo/find-by-email @test-ds "nobody@example.com"))))

(deftest create-and-find-user
  (testing "creates a user and returns it"
    (let [user (repo/create! @test-ds {:email "alice@example.com"
                                       :password "hashed-pw"
                                       :role "user"})]
      (is (= "alice@example.com" (:users/email user)))
      (is (= "user" (:users/role user)))
      (is (some? (:users/id user)))))

  (testing "find-by-email returns the created user"
    (repo/create! @test-ds {:email "bob@example.com" :password "pw" :role "user"})
    (let [found (repo/find-by-email @test-ds "bob@example.com")]
      (is (= "bob@example.com" (:users/email found)))))

  (testing "find-all returns all users"
    (repo/create! @test-ds {:email "user1@example.com" :password "pw" :role "user"})
    (repo/create! @test-ds {:email "user2@example.com" :password "pw" :role "user"})
    (is (>= (count (repo/find-all @test-ds)) 2))))
