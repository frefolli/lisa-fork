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
}
