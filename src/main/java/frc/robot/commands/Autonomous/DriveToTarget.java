/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.Autonomous;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj2.command.PIDCommand;
import frc.robot.Constants;
import frc.robot.subsystems.DriveTrain;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/latest/docs/software/commandbased/convenience-features.html
public class DriveToTarget extends PIDCommand {
  /**
   * Creates a new TrackTarget.
   */
  private DriveTrain d;
  
  public DriveToTarget(DriveTrain d) {
    super(
        // The controller that the command will use
        new PIDController(.1, 0.05, 0),
        // This should return the measurement
        () -> Constants.ta.getDouble(0.0),
        // This should return the setpoint (can also be a constant)
        () -> 2.8,
        // This uses the output
        output -> {
          d.arcadeDrive(output, 0);
        });
    addRequirements(d);
    getController().setTolerance(1);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return getController().atSetpoint();
  }
}
