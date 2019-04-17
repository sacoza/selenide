package com.codeborne.selenide.proxy;

public interface Disableable {
  void disable();
  void enable();
  boolean isEnabled();
}
