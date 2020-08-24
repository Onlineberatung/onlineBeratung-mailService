package de.caritas.cob.mailservice.api.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.io.PrintWriter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;

@RunWith(MockitoJUnitRunner.class)
public class LogServiceTest {

  private final String ERROR_MESSAGE = "error message";

  @Mock
  private Logger logger;

  @Before
  public void setup() {
    setInternalState(LogService.class, "LOGGER", logger);
  }

  @Test
  public void logError_Should_LogExceptionStackTrace() {
    Exception exception = Mockito.mock(Exception.class);
    LogService.logError(ERROR_MESSAGE, exception);
    verify(logger, atLeastOnce()).error(anyString(), eq(ERROR_MESSAGE));
    verify(exception, atLeastOnce()).printStackTrace(any(PrintWriter.class));
  }

  @Test
  public void logError_Should_LogErrorMessage() {
    LogService.logError(ERROR_MESSAGE);
    verify(logger, atLeastOnce()).error(anyString(), eq(ERROR_MESSAGE));
  }

  @Test
  public void logDebug_Should_LogDebugMessage() {
    LogService.logDebug(ERROR_MESSAGE);
    verify(logger, atLeastOnce()).debug(eq(ERROR_MESSAGE));
  }

  @Test
  public void logWarn_Should_LogWarnMessage() {
    LogService.logWarn(ERROR_MESSAGE);
    verify(logger, atLeastOnce()).warn(eq(ERROR_MESSAGE));
  }


}
