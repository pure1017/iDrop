import os
basedir = os.path.abspath(os.path.dirname(__file__))


class Auth:
    CLIENT_ID = '404268012776-for6fmuj3um5i19k694gmd01ne0sd2fp.apps.googleusercontent.com'
    CLIENT_SECRET = 'jm-ZOYMMptezlm0tFs6UNRnS'
    AUTH_URI = 'https://accounts.google.com/o/oauth2/auth'
    TOKEN_URI = 'https://accounts.google.com/o/oauth2/token'
    USER_INFO = 'https://www.googleapis.com/userinfo/v2/me'
    SCOPE = ['profile', 'email']


class Config:
    APP_NAME = 'Less0n'
    SECRET_KEY = os.environ.get('CLIENT_SECRET') or '!Qazxsw2#Edcvfr4%Tgbnhy6&Ujm,ki8(Ol./;p0'
    SQLALCHEMY_TRACK_MODIFICATIONS = True


class DevConfig(Config):
    DEBUG = True
    SQLALCHEMY_ECHO = True
    SQLALCHEMY_DATABASE_URI = 'sqlite:///./less0n.db'
    GOOGLE_OAUTH_REDIRECT_URI = 'https://localhost:5000/oauth'


class TestConfig(Config):
    DEBUG = True
    SQLALCHEMY_DATABASE_URI = 'postgresql://postgres:!Qazxsw2#Edcvfr4@localhost/postgres?sslmode=disable'
    GOOGLE_OAUTH_REDIRECT_URI = 'https://localhost:5000/oauth'


class ProdConfig(Config):
    DEBUG = False
    # SQLALCHEMY_DATABASE_URI = 'postgresql+psycopg2://postgres:!Qazxsw2#Edcvfr4@/postgres?host=/cloudsql/swift-hope-195519:us-central1:less0n'  # Google Cloud
    SQLALCHEMY_DATABASE_URI = os.getenv('DATABASE_URL')  # Heroku
    GOOGLE_OAUTH_REDIRECT_URI = 'https://less0n.bmao.tech/oauth'


config = {
    "dev": DevConfig,
    "test": TestConfig,
    "prod": ProdConfig,
    "default": DevConfig
}
