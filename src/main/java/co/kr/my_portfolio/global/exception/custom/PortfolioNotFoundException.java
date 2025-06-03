package co.kr.my_portfolio.global.exception.custom;

public class PortfolioNotFoundException extends RuntimeException {
    public PortfolioNotFoundException(String message) {
        super(message);
    }
}
