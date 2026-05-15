package it.unive.lisa.analysis.avase;

import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.util.representation.ListRepresentation;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;
import it.unive.lisa.util.representation.StructuredObject;

/**
 * An implementation of a type branch=(condition, choice) that can be used as part of a Control Branch or Control Dependencies or whatever.
 * 
 * @author <a href="mailto:f.refolli@campus.unimib.it">Francesco Refolli</a>
 */
public class Branch
		implements StructuredObject {
  private final ProgramPoint condition;
  private final Boolean choice;

	public Branch() {
		this(null, null);
	}

	public Branch(ProgramPoint condition, Boolean choice) {
    if ((condition == null && choice != null) || (condition != null && choice == null)) {
      throw new AvaseImplException("If condition or choice are null then the other one should be null to construct a proper Branch::Bottom object");
    }
    this.condition = condition;
    this.choice = choice;
	}

  public ProgramPoint getCondition() {
    return condition;
  }

  public Boolean getChoice() {
    return choice;
  }

  public boolean isBottom() {
    return condition == null && choice == null;
  }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((condition == null) ? 0 : condition.hashCode());
		result = prime * result + ((choice == null) ? 0 : choice.hashCode());
		return result;
	}

	@Override
	public boolean equals(
			Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Branch other = (Branch) obj;
		if (condition == null) {
			if (other.condition != null)
				return false;
		} else if (!condition.equals(other.condition))
			return false;
		if (choice == null) {
			if (other.choice != null)
				return false;
		} else if (!choice.equals(other.choice))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return representation().toString();
	}

	@Override
	public StructuredRepresentation representation() {
		return new ListRepresentation(new StringRepresentation(condition), new StringRepresentation(choice));
	}
}
