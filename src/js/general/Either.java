package js.general;

public class Either<L, R> {

    private final boolean isLeft;
    private final L left;
    private final R right;
    
    public Either(boolean isLeft, L left, R right) {
	this.isLeft = isLeft;
	this.left = left;
	this.right = right;
    }

    public boolean isLeft() {
	return isLeft;
    }

    public boolean isRight() {
	return !isLeft;
    }

    public L getLeft() {
	if(!isLeft) {
	    throw new RuntimeException("Can not access Left, if Either is Either.Right");
	}
	return left;
    }
    
    public R getRight() {
	if(isLeft) {
	    throw new RuntimeException("Can not access Right, if Either is Either.Left");
	}
	return right;
    }
    
    public static <L, R> Either<L, R> left(L l) {
	return new Either<L, R>(true, l, null);
    }

    public static <L, R> Either<L, R> right(R r) {
	return new Either<L, R>(false, null, r);
    }
}
