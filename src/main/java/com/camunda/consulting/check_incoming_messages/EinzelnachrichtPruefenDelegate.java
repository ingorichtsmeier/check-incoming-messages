package com.camunda.consulting.check_incoming_messages;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Named;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

@Named
public class EinzelnachrichtPruefenDelegate implements JavaDelegate {

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    String nachricht = (String) execution.getVariable("nachricht");
    Pattern pattern = Pattern.compile("^[a-zA-Z ]*([0-9]*)[a-zA-Z ]*$");
    Matcher matcher = pattern.matcher(nachricht);
    boolean find = matcher.find();
    Long number = Long.valueOf(matcher.group(1));
    String nachrichtOK = "OK";
    if (number >= 40 && number < 50) {
      nachrichtOK = "Fehler";
    } else if (number >= 50) {
      nachrichtOK = "Korrektur";
    }
    execution.setVariable("nachrichtOK", nachrichtOK);
  }

}
