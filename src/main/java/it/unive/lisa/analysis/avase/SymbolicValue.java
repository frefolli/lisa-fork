package it.unive.lisa.analysis.avase;

import jbse.val.Primitive;
import it.unive.lisa.util.representation.ListRepresentation;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;
import it.unive.lisa.util.representation.StructuredObject;

public class SymbolicValue implements StructuredObject {
  public final Primitive value;
  public final Primitive condition;

  public SymbolicValue(Primitive value, Primitive condition) {
    this.value = value;
    this.condition = condition;
  }

	@Override
	public String toString() {
		return representation().toString();
	}

	@Override
	public StructuredRepresentation representation() {
		return new ListRepresentation(new StringRepresentation(value), new StringRepresentation(condition));
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
		SymbolicValue other = (SymbolicValue) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		if (condition == null) {
			if (other.condition != null)
				return false;
		} else if (!condition.equals(other.condition))
			return false;
		return true;
	}
}
