/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private Command m_autonomousCommand;

  private RobotContainer m_robotContainer;

  private final I2C.Port i2cPost = I2C.Port.kOnboard;

  private final ColorSensorV3 m_colorSensor = new ColorSensorV3(i2cPost);
  private final ColorMatch m_colorMatcher = new ColorMatch();

  private final Color kBlueTarget = ColorMatch.makeColor(0.143, 0.427, 0.429);
  private final Color kGreenTarget = ColorMatch.makeColor(0.197, 0.561, 0.240);
  private final Color kRedTarget = ColorMatch.makeColor(0.561, 0.232, 0.114);
  private final Color kYellowTarget = ColorMatch.makeColor(0.361, 0.524, 0.113);
  public String colorString;

  public String gameData = DriverStation.getInstance().getGameSpecificMessage();

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {

    m_robotContainer = new RobotContainer();

    m_robotContainer.m_limelight.lightOff();

    m_robotContainer.m_cp.init();
    m_robotContainer.m_climb.init();
    m_robotContainer.m_intake.init();

    m_colorMatcher.addColorMatch(kBlueTarget);
    m_colorMatcher.addColorMatch(kGreenTarget);
    m_colorMatcher.addColorMatch(kRedTarget);
    m_colorMatcher.addColorMatch(kYellowTarget);

    m_robotContainer.m_drive.front_L.setIdleMode(IdleMode.kCoast);
    m_robotContainer.m_drive.front_R.setIdleMode(IdleMode.kCoast);
    m_robotContainer.m_drive.back_R.setIdleMode(IdleMode.kCoast);
    m_robotContainer.m_drive.back_L.setIdleMode(IdleMode.kCoast);

  }

  

  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    // Runs the Scheduler.  This is responsible for polling buttons, adding newly-scheduled
    // commands, running already-scheduled commands, removing finished or interrupted commands,
    // and running subsystem periodic() methods.  This must be called from the robot's periodic
    // block in order for anything in the Command-based framework to work.
    CommandScheduler.getInstance().run();

    Color detectedColor = m_colorSensor.getColor();

    ColorMatchResult match = m_colorMatcher.matchClosestColor(detectedColor);

    if (match.color == kBlueTarget) {
      colorString = "Blue";
    } else if (match.color == kRedTarget) {
      colorString = "Red";
    } else if (match.color == kGreenTarget) {
      colorString = "Green";
    } else if (match.color == kYellowTarget) {
      colorString = "Yellow";
    } else {
      colorString = "Unknown";
    }

    m_robotContainer.m_limelight.lightOn();

    /**
     * Open Smart Dashboard or Shuffleboard to see the color detected by the 
     * sensor.
     */
    SmartDashboard.putNumber("Red", detectedColor.red);
    SmartDashboard.putNumber("Green", detectedColor.green);
    SmartDashboard.putNumber("Blue", detectedColor.blue);
    SmartDashboard.putNumber("Confidence", match.confidence);
    SmartDashboard.putString("Detected Color", colorString);

    SmartDashboard.putNumber("RPM", m_robotContainer.m_shoot.shooter.getEncoder().getVelocity());

    if(DriverStation.getInstance().getMatchTime() >= 30){
      m_robotContainer.m_climb.switch_climb.set(Value.kForward);
    }
    SmartDashboard.putNumber("Time", DriverStation.getInstance().getMatchTime());
    
    if(m_robotContainer.m_climb.switch_climb.get() == Value.kReverse){
      m_robotContainer.m_intake.intakeSOL.set(Value.kReverse);
      m_robotContainer.m_cp.control_solenoid.set(Value.kForward);
      m_robotContainer.m_shoot.shooterSOL.set(Value.kReverse);
    }
    SmartDashboard.putString("CP Position", m_robotContainer.m_cp.getCPPosition());
    SmartDashboard.putString("Shooter Position", m_robotContainer.m_shoot.getShooterPosition());
    /**
     * [(Vcc/1024) = Vcm]
     * Vcc = Supplied Voltage
     * Vcm = Volts per cm (Scaling)
     */

    if(m_robotContainer.m_drive.front_R.getIdleMode() == IdleMode.kCoast){
      SmartDashboard.putBoolean("Drive Mode", false);
    }else{
      SmartDashboard.putBoolean("Drive Mode", true);
    } 

    String gameData;
gameData = DriverStation.getInstance().getGameSpecificMessage();
if(gameData.length() > 0)
{
  switch (gameData.charAt(0))
  {
    case 'B' :
      if(colorString == "Blue"){
      m_robotContainer.m_cp.control_motor.stopMotor();
      }
      break;
    case 'G' :
    if(colorString == "Green"){
    m_robotContainer.m_cp.control_motor.stopMotor();
    }
      break;
    case 'R' :
    if(colorString == "Red"){
    m_robotContainer.m_cp.control_motor.stopMotor();
    }
      break;
    case 'Y' :
    if(colorString == "Yellow"){
    m_robotContainer.m_cp.control_motor.stopMotor();
    }
      break;
  }
}

    //if(Constants.tv.getDouble(0.0) == 0.0){
      //m_robotContainer.m_shoot.shooterSOL.set(Value.kForward);
    //}
    //if(Constants.tv.getDouble(0.0) == 1.0){
      //m_robotContainer.m_shoot.shooterSOL.set(Value.kReverse);
    //}

    SmartDashboard.putNumber("Left", m_robotContainer.m_drive.getLeftDistance());
    SmartDashboard.putNumber("Right", m_robotContainer.m_drive.getRightDistance());
  }

  public String getColorData(){
    return gameData;
  }

  public String getColor(){
    return colorString;
  }

  /**
   * This function is called once each time the robot enters Disabled mode.
   */
  @Override
  public void disabledInit() {
  }

  @Override
  public void disabledPeriodic() {
  }

  /**
   * This autonomous runs the autonomous command selected by your {@link RobotContainer} class.
   */
  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    // schedule the autonomous command (example)
    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void teleopInit() {
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
  }

  @Override
  public void testInit() {
    // Cancels all running commands at the start of test mode.
    CommandScheduler.getInstance().cancelAll();
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
