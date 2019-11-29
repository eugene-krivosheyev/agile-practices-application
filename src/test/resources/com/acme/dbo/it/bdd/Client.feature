@FR
Feature: Get all Clients
  As a user, I want get all clients details, for accounting report.

  Background:
    Given application is running

  @tx
  Scenario: No clients
    Given application has no stored clients
    When user requests all clients
    Then user got no clients

  @tx
  Scenario: Has clients
    Given application has stored clients
      And client stored with login 'new@mail.com', secret '123456789', salt '123456'
    When user requests all clients
    Then user got clients
      And client got with login 'new@mail.com', secret '123456789', salt '12345', enabled
      And client got with login 'admin@acme.com', secret 'c99ef573720e30031034d24e82721350dfa6af9957d267c2acc0be98813bb3e4', salt 'somesalt', enabled
      And client got with login 'account@acme.com', secret '5aba80f0c9f7cfb0c7e8d5767aad85e8b384872e070c13a8fe6d11f58327934b', salt 'somesalt', enabled
      And client got with login 'disabled@acme.com', secret '7a2d0aa3d45a06277ee9e48623ae0dc1d9d5f83948a0b3e5cba3cae4fda533f7', salt 'somesalt', disabled
