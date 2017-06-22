package br.com.gabrielberlanda.spotifyvoicecontroller;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EActivity(R.layout.activity_spotify_voice_controller)
public class SpotifyVoiceControllerActivity extends AppCompatActivity
{
    //    █████╗ ████████╗████████╗██████╗ ██╗██████╗ ██╗   ██╗████████╗███████╗███████╗
    //   ██╔══██╗╚══██╔══╝╚══██╔══╝██╔══██╗██║██╔══██╗██║   ██║╚══██╔══╝██╔════╝██╔════╝
    //   ███████║   ██║      ██║   ██████╔╝██║██████╔╝██║   ██║   ██║   █████╗  ███████╗
    //   ██╔══██║   ██║      ██║   ██╔══██╗██║██╔══██╗██║   ██║   ██║   ██╔══╝  ╚════██║
    //   ██║  ██║   ██║      ██║   ██║  ██║██║██████╔╝╚██████╔╝   ██║   ███████╗███████║
    //   ╚═╝  ╚═╝   ╚═╝      ╚═╝   ╚═╝  ╚═╝╚═╝╚═════╝  ╚═════╝    ╚═╝   ╚══════╝╚══════╝

    /**
     * Handle de solicitação de permissão
     */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    /**
     *
     */
    final static String SPOTIFY_PACKAGE_NAME = "com.spotify.music";

    /**
     * Spotify media controller
     */
    MediaController spotifyController;

    /**
     *
     */
    @Bean
    SpotifyVoiceControllerRecognitionListener spotifyVoiceControllerRecognitionListener;

    /**
     *
     */
    @ViewById( R.id.playButton )
    Button playButton;

    /**
     *
     */
    @ViewById( R.id.pauseButton )
    Button pauseButton;

    /**
     *
     */
    @ViewById( R.id.nextTrackButton )
    Button nextTrackButton;

    /**
     *
     */
    @ViewById( R.id.previousTrackButton )
    Button previousTrackButton;

    /**
     *
     */
    @ViewById( R.id.messageTextView )
    TextView message;

    //    ██████╗ ███╗   ██╗    ██╗███╗   ██╗██╗████████╗
    //   ██╔═══██╗████╗  ██║    ██║████╗  ██║██║╚══██╔══╝
    //   ██║   ██║██╔██╗ ██║    ██║██╔██╗ ██║██║   ██║
    //   ██║   ██║██║╚██╗██║    ██║██║╚██╗██║██║   ██║
    //   ╚██████╔╝██║ ╚████║    ██║██║ ╚████║██║   ██║
    //    ╚═════╝ ╚═╝  ╚═══╝    ╚═╝╚═╝  ╚═══╝╚═╝   ╚═╝

    /**
     * On Init da Activity
     */
    @AfterViews
    public void onAfterViews()
    {
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO );
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            return;
        }
        this.checkAndSetSpotifyMediaController();
    }

    //   ██████╗ ███████╗██╗  ██╗ █████╗ ██╗   ██╗██╗ ██████╗ ██████╗ ███████╗
    //   ██╔══██╗██╔════╝██║  ██║██╔══██╗██║   ██║██║██╔═══██╗██╔══██╗██╔════╝
    //   ██████╔╝█████╗  ███████║███████║██║   ██║██║██║   ██║██████╔╝███████╗
    //   ██╔══██╗██╔══╝  ██╔══██║██╔══██║╚██╗ ██╔╝██║██║   ██║██╔══██╗╚════██║
    //   ██████╔╝███████╗██║  ██║██║  ██║ ╚████╔╝ ██║╚██████╔╝██║  ██║███████║
    //   ╚═════╝ ╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝  ╚═══╝  ╚═╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝

    /**
     * Método que verifica se possui algum media controler do spotifiy no dispositivo.
     */
    public void checkAndSetSpotifyMediaController()
    {
        final MediaSessionManager mediaSessionManager =
                (MediaSessionManager) this.getSystemService( Context.MEDIA_SESSION_SERVICE);
        final List<MediaController> mediaControllers = mediaSessionManager.getActiveSessions( new ComponentName(this, NotificationListener.class) );

        for ( MediaController mediaController: mediaControllers )
        {
            if( mediaController.getPackageName().equals( br.com.gabrielberlanda.spotifyvoicecontroller.SpotifyVoiceControllerActivity.SPOTIFY_PACKAGE_NAME ) )
            {
                this.spotifyController = mediaController;
                Toast.makeText(this, "Media controller do spotify encontrado.", Toast.LENGTH_SHORT).show();
                break;
            }

            if ( this.spotifyController == null )
            {
                this.finish();
            }
        }
    }

    /**
     * Atualizar a mensagem da tela
     */
    @UiThread
    public void updateDisplayedMessage( String message )
    {
        this.message.setText( message );
    }

    /**
     * Handler do evento de click no botão de tocar
     */
    @Click( R.id.playButton )
    public void onPlayTrack()
    {
        this.spotifyController.getTransportControls().play();
    }

    /**
     *  Handler do evento de click no botão de pausar
     */
    @Click( R.id.pauseButton )
    public void onPauseTrack()
    {
        this.spotifyController.getTransportControls().pause();
    }

    /**
     * Handler do evento de click no botão de próxima música
     */
    @Click( R.id.nextTrackButton )
    public void onNextTrack()
    {
        this.spotifyController.getTransportControls().skipToNext();
    }

    /**
     * Handler do evento de click no botão de voltar música
     */
    @Click( R.id.previousTrackButton )
    public void onPreviousTrack()
    {
        this.spotifyController.getTransportControls().skipToPrevious();
    }

}

