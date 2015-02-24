package org.usfirst.frc.team1554.lib.math;

public interface Vector<T extends Vector<T>> {

    T set(T v);

    T cpy();

    double len();

    double len2();

    double dst(T v);

    double dst2(T v);

    T limit(double limit);

    T limit2(double limit2);

    T setLength(double len);

    T setLength2(double len2);

    T clamp(double min, double max);

    T add(T v);

    T sub(T v);

    double dot(T v);

    T mulAdd(T v, double scalar);

    T mulAdd(T v, T scl);

    T nor();

    T scale(double scalar);

    T scale(T v);

    T lerp(T target, double alpha);

    boolean isUnit();

    boolean isUnit(double epsilon);

    boolean isZero();

    boolean isZero(double epsilon);

    boolean isInline(T other);

    boolean isInline(T other, double epsilon);

    boolean isColinear(T other);

    boolean isColinear(T other, double epsilon);

    boolean isColinearOpposite(T other);

    boolean isColinearOpposite(T other, double epsilon);

    boolean isPerpendicular(T other);

    boolean isPerpendicular(T other, double epsilon);

    boolean inSameDirection(T v);

    boolean inOppositeDirection(T v);

    boolean equalsEpsilon(T v, double epsilon);

    T zero();

}
