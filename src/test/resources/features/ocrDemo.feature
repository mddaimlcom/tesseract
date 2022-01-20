Feature: ocr Demo

  Scenario Outline: Google Translate buttons text extraction
    Given user is on Google Translate page
    When user selects <language> Language
    And user types <word> via ocr engine
    Then <word> is present in text area
    Examples:
      | word          | language |
      | Demonstration | English  |
      | Демонстрация  | Russian  |

    Scenario: Captcha text extraction
      Given user is on Fake Captcha page
      When user creates fake captcha for 'Tesseract 123'
      Then the word from captcha corresponds to typed one