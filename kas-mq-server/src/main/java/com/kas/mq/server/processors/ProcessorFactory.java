package com.kas.mq.server.processors;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.kas.infra.utils.StringUtils;
import com.kas.mq.impl.messages.IMqMessage;
import com.kas.mq.internal.ERequestType;
import com.kas.mq.internal.PackageRegistrar;
import com.kas.mq.server.IRepository;
import com.kas.mq.server.internal.SessionHandler;

/**
 * A class that generates {@link IProcessor processor} objects to handle requests.
 * 
 * @author Pippo
 * 
 * @see com.kas.mq.server.processors.IProcessor
 */
public class ProcessorFactory extends PackageRegistrar<ERequestType, Constructor<?>>
{
  /**
   * Logger
   */
  static private Logger sLogger = LogManager.getLogger(ProcessorFactory.class);
  
  /**
   * Construct this command factory
   * 
   * @param pkg
   *   The name of the package
   */
  protected ProcessorFactory(String pkg)
  {
    super(pkg);
  }
  
  /**
   * Register class {@code cls}.
   * <br>
   * We test {@code cls} to be a {@link ICommand}. If it is, we register it within the factory.
   * 
   * @param cls
   *   The class that needs initialization.
   */
  protected void register(Class<?> cls)
  {
    if (isProcessorDrivenClass(cls))
    {
      registerProcessorClass(cls);
    }
  }
  
  /**
   * Test if {@code cls} is a driven, non-abstract instance of {@link IProcessor}
   * 
   * @param cls
   *   The class object to test
   * @return
   *   {@code true} if {@code cls} is an {@link IProcessor}, {@code false} otherwise
   */
  public boolean isProcessorDrivenClass(Class<?> cls)
  {
    int mods = cls.getModifiers();
    if (!IProcessor.class.isAssignableFrom(cls))
      return false;
    if (Modifier.isAbstract(mods))
      return false;
    if (Modifier.isInterface(mods))
      return false;
    return true;
  }
  
  /**
   * Registering a {@code cls} with its verbs
   * 
   * @param cls
   *   The command class implementing {@link ICommand} interface
   * @return
   *   {@code true} if command registered successfully, {@code false} otherwise
   */
  private boolean registerProcessorClass(Class<?> cls)
  {
    sLogger.trace("ProcessorFactory::registerProcessorClass() - IN, Class=[{}]", cls.getName());
    
    boolean success = false;
    try
    {
      Constructor<?> ctor = cls.getDeclaredConstructor(IMqMessage.class, SessionHandler.class, IRepository.class);
      boolean accessible = ctor.isAccessible();
      ctor.setAccessible(true);
      Object obj = ctor.newInstance();
      ctor.setAccessible(accessible);
      IProcessor proc = (IProcessor)obj;
      
      ERequestType type = proc.getRequestType();
      
      sLogger.debug("ProcessorFactory::registerProcessorClass() - Registering RequestType=[{}] with Processor=[{}]", type, cls.getName());
      register(type, ctor);
      success = true;
    }
    catch (Throwable e) {}
    
    sLogger.trace("ProcessorFactory::registerProcessorClass() - OUT, Success=[{}]", success);
    return success;
  }

  public IProcessor newProcessor(IMqMessage request, SessionHandler handler, IRepository repository)
  {
    sLogger.trace("ProcessorFactory::newProcessor() - IN");
    
    IProcessor processor = null;
    
    ERequestType type = request.getRequestType();
    sLogger.trace("ProcessorFactory::newProcessor() - RequestType is: {}", type);
    
    Constructor<?> ctor = mRegistration.get(type);
    
//    switch (type)
//    {
//      case cLogin:
        processor = new LoginProcessor(request, handler, repository);
//        break;
//      case cDefineGroup:
//        processor = new DefineGroupProcessor(request, handler, repository);
//        break;
//      case cDeleteGroup:
//        processor = new DeleteGroupProcessor(request, handler, repository);
//        break;
//      case cDefineUser:
//        processor = new DefineUserProcessor(request, handler, repository);
//        break;
//      case cDeleteUser:
//        processor = new DeleteUserProcessor(request, handler, repository);
//        break;
//      case cDefineQueue:
//        processor = new DefineQueueProcessor(request, handler, repository);
//        break;
//      case cDeleteQueue:
//        processor = new DeleteQueueProcessor(request, handler, repository);
//        break;
//      case cAlterQueue:
//        processor = new AlterQueueProcessor(request, handler, repository);
//        break;
//      case cQueryServer:
//        processor = new QueryServerProcessor(request, handler, repository);
//        break;
//      case cNotifyRepoUpdate:
//        processor = new RepoUpdateProcessor(request, handler, repository);
//        break;
//      case cNotifySysState:
//        processor = new SysStateProcessor(request, handler, repository);
//        break;
//      case cTermServer:
//        processor = new TermServerProcessor(request, handler, repository);
//        break;
//      case cGet:
//        processor = new MessageGetProcessor(request, handler, repository);
//        break;
//      case cTermConn:
//        processor = new TermConnectionProcessor(request, handler, repository);
//        break;
//      case cTermSess:
//        processor = new TermSessionProcessor(request, handler, repository);
//        break;
//      case cUnknown:
//      default:
//        processor = new MessagePutProcessor(request, handler, repository);
//        break;
//    }
    
    sLogger.trace("ProcessorFactory::newProcessor() - OUT, Returns={}", StringUtils.asPrintableString(processor));
    return processor;
  }
}
