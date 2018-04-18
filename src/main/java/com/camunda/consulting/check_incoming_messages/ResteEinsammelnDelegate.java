package com.camunda.consulting.check_incoming_messages;

import java.util.List;

import javax.inject.Named;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class ResteEinsammelnDelegate implements JavaDelegate {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(ResteEinsammelnDelegate.class);

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    String nachrichtKorrigiert = (String) execution.getVariable("nachrichtKorrigiert");
    LOGGER.info("korrigierte nachricht {}", nachrichtKorrigiert);
    @SuppressWarnings("unchecked")
    List<String> lieferliste = (List<String>) execution.getVariable("lieferliste");
    LOGGER.info("lieferliste: {}", lieferliste);
    for (int i = 0; i < lieferliste.size(); i++) {
      String lieferelement = lieferliste.get(i);
      if (lieferelement == null) {
        LOGGER.info("Element ersetzen an {}", i);
        lieferliste.set(i, nachrichtKorrigiert);
        break;
      }
    }
    LOGGER.info("neue Lieferliste: {}", lieferliste);
    execution.setVariable("lieferliste", lieferliste);
  }
}
