package it.unive.lisa.analysis.avase;

import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.util.representation.ListRepresentation;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;
import it.unive.lisa.util.representation.StructuredObject;

/**
 * An implementation of a type definition=(identifier, program_point) that can be used as part of a reaching definition.
 * 
 * @author <a href="mailto:f.refolli@campus.unimib.it">Francesco Refolli</a>
 */
public class Definition
		implements StructuredObject {
  public final Identifier variable;
  public final ProgramPoint programPoint;

	public Definition() {
		this(null, null);
	}

	public Definition(
			Identifier variable,
			ProgramPoint pp) {
		this.programPoint = pp;
		this.variable = variable;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((programPoint == null) ? 0 : programPoint.hashCode());
		result = prime * result + ((variable == null) ? 0 : variable.hashCode());
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
		Definition other = (Definition) obj;
		if (programPoint == null) {
			if (other.programPoint != null)
				return false;
		} else if (!programPoint.equals(other.programPoint))
			return false;
		if (variable == null) {
			if (other.variable != null)
				return false;
		} else if (!variable.equals(other.variable))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return representation().toString();
	}

	@Override
	public StructuredRepresentation representation() {
		return new ListRepresentation(new StringRepresentation(variable), new StringRepresentation(programPoint));
	}
}
