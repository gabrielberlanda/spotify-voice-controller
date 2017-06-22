package br.com.gabrielberlanda.spotifyvoicecontroller;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;

import java.io.File;
import java.io.IOException;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

/**
 * Created by berlanda on 20/06/17.
 */
@EBean
public class SpotifyVoiceControllerRecognitionListener implements RecognitionListener
{

    //    █████╗ ████████╗████████╗██████╗ ██╗██████╗ ██╗   ██╗████████╗███████╗███████╗
    //   ██╔══██╗╚══██╔══╝╚══██╔══╝██╔══██╗██║██╔══██╗██║   ██║╚══██╔══╝██╔════╝██╔════╝
    //   ███████║   ██║      ██║   ██████╔╝██║██████╔╝██║   ██║   ██║   █████╗  ███████╗
    //   ██╔══██║   ██║      ██║   ██╔══██╗██║██╔══██╗██║   ██║   ██║   ██╔══╝  ╚════██║
    //   ██║  ██║   ██║      ██║   ██║  ██║██║██████╔╝╚██████╔╝   ██║   ███████╗███████║
    //   ╚═╝  ╚═╝   ╚═╝      ╚═╝   ╚═╝  ╚═╝╚═╝╚═════╝  ╚═════╝    ╚═╝   ╚══════╝╚══════╝

    /**
     * Application root
     */
    @RootContext
    SpotifyVoiceControllerActivity spotifyVoiceControllerActivity;

    /**
     *
     */
    SpeechRecognizer recognizer;

    //----------------------------------------------------------------
    // COMANDO DE VOZ DAS 'AÇÕES'
    //---------------------------------------------------------------
    /**
     *
     */
    static final String HOT_WORD = "HOTWORD";

    /**
     *
     */
    static final String ACTIONS = "ACTIONS";

    //----------------------------------------------------------------
    // COMANDOS DE VOZ PARA A APLICAÇÃO
    //---------------------------------------------------------------

    /**
     *
     */
    static final String HOT_WORD_PHRASE = "hello my app";

    /**
     * Comando de voz para ativar o tocar
     */
    static final String PLAY_ACTION_VOICE_COMMAND = "play please";

    /**
     * Comando de voz para ativar o pausar
     */
    static final String PAUSE_ACTION_VOICE_COMMAND = "pause please";

    /**
     * Comando de voz para ativar o próxima música
     */
    static final String NEXT_ACTION_VOICE_COMMAND = "next please";

    /**
     * Comando de voz para ativar o voltar música
     */
    static final String PREVIOUS_ACTION_VOICE_COMMAND = "previous please";

    //--------------------------------------------
    // Constructor
    //-------------------------------------------
    /**
     *
     */
    public SpotifyVoiceControllerRecognitionListener()
    {
    }

    //   ██████╗ ███████╗██╗  ██╗ █████╗ ██╗   ██╗██╗ ██████╗ ██████╗ ███████╗
    //   ██╔══██╗██╔════╝██║  ██║██╔══██╗██║   ██║██║██╔═══██╗██╔══██╗██╔════╝
    //   ██████╔╝█████╗  ███████║███████║██║   ██║██║██║   ██║██████╔╝███████╗
    //   ██╔══██╗██╔══╝  ██╔══██║██╔══██║╚██╗ ██╔╝██║██║   ██║██╔══██╗╚════██║
    //   ██████╔╝███████╗██║  ██║██║  ██║ ╚████╔╝ ██║╚██████╔╝██║  ██║███████║
    //   ╚═════╝ ╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝  ╚═══╝  ╚═╝ ╚═════╝ ╚═╝  ╚═╝╚══════╝

    /**
     * Chamada para o inicio do setup do Recognition Listener
     */
    @AfterInject
    @Background
    public void runSetup()
    {
        try
        {
            final Assets assets = new Assets( spotifyVoiceControllerActivity );
            final File assetDir = assets.syncAssets();
            setup( assetDir );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param assetsDir
     */
    private void setup( File assetsDir ) throws IOException
    {
        this.recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel( new File( assetsDir, "en-us-ptm" ) )
                .setDictionary( new File( assetsDir, "words-dictionary.dict" ) )
                .getRecognizer();

        recognizer.addListener( this );

        recognizer.addKeyphraseSearch( HOT_WORD , HOT_WORD_PHRASE );

        //Adicionamos as actions
        final File actionsGrammar = new File( assetsDir, "actions.gram" );
        recognizer.addGrammarSearch( ACTIONS, actionsGrammar );

        //Inicia o evento de busca por nossa hotword
        this.onFinishSetup();
    }

    /**
     *
     */
    private void search( String searchFor )
    {
        this.recognizer.stop();

        //Se acaso estivermos buscando por nossa 'hot word' nós não precisamos de timeout
        if ( searchFor.equals(HOT_WORD) )
        {
            this.spotifyVoiceControllerActivity.updateDisplayedMessage( "Estamos buscando pela palavra chave: " + HOT_WORD_PHRASE + "..." );
            this.recognizer.startListening( HOT_WORD );
        }
        else if ( searchFor.equals( ACTIONS ) )
        {
            //Timeout de 5segundos
            this.spotifyVoiceControllerActivity.updateDisplayedMessage( "O que vou fazer?" );
            this.recognizer.startListening( ACTIONS, 3000 );
        }
    }

    /**
     *
     */
    @UiThread
    public void onFinishSetup()
    {
        //Depois de finalizarmos o setup, iniciamos a busca por nossa HOTWORD
        Toast.makeText(spotifyVoiceControllerActivity, "Terminamos o setup", Toast.LENGTH_SHORT).show();
        this.searchForHotWord();
    }

    /**
     *
     */
    @Override
    public void onBeginningOfSpeech()
    {
        Log.wtf("RecognitionListener", "onBeginningOfSpeech()");
    }

    /**
     *
     */
    @Override
    public void onEndOfSpeech()
    {
        Log.wtf( "RecognitionListener", "onEndOfSpeech()" );
        if ( !this.recognizer.getSearchName().equals( HOT_WORD ) ) this.searchForHotWord();
    }

    /**
     *
     * @param hypothesis
     */
    @Override
    public void onPartialResult(Hypothesis hypothesis)
    {
        if ( hypothesis == null ) return;

        String textSaid = hypothesis.getHypstr();

        if ( textSaid.equals( HOT_WORD_PHRASE ) )
        {
            this.searchForActions();
        }
        else
        {
            //Aqui vamos verificar se já foi falado a palavra que estamos buscando,
            //não necessariamente precisamos esperar cair no timeOUT
            this.checkForActions( textSaid );
        }
    }

    /**
     *
     * @param hypothesis
     */
    @Override
    public void onResult(Hypothesis hypothesis)
    {
        Log.wtf("RecognitionListener", "onResult()");
        if ( hypothesis != null )
        {
            final String textSaid = hypothesis.getHypstr();

            this.checkForActions( textSaid );
        }
    }

    /**
     * Busca por ações do sistema :D
     */
    public void checkForActions( String whatToDo )
    {
        Log.wtf("LOG", "Estamos buscando ação para: " + whatToDo );

        Boolean actionFound = false;

        switch ( whatToDo )
        {
            case PLAY_ACTION_VOICE_COMMAND:
                actionFound = true;
                this.spotifyVoiceControllerActivity.onPlayTrack();
                break;
            case PAUSE_ACTION_VOICE_COMMAND:
                actionFound = true;
                this.spotifyVoiceControllerActivity.onPauseTrack();
                break;
            case NEXT_ACTION_VOICE_COMMAND:
                actionFound = true;
                this.spotifyVoiceControllerActivity.onNextTrack();
                break;
            case PREVIOUS_ACTION_VOICE_COMMAND:
                actionFound = true;
                this.spotifyVoiceControllerActivity.onPreviousTrack();
                break;
            default:
                Log.wtf("LOG", "Não encontramos nenhuma ação para: " + whatToDo );
        }

        if ( actionFound )
        {
            this.searchForHotWord();
        }
    }

    /**
     *
     * @param e
     */
    @Override
    public void onError(Exception e)
    {
        Log.wtf("RecognitionListener", "onError()");
        e.printStackTrace();
    }

    /**
     *
     */
    @Override
    public void onTimeout()
    {
        Log.wtf("RecognitionListener", "onTimeout()");
        this.searchForHotWord();
    }

    /**
     * Busca pela palavra chave
     */
    private void searchForHotWord()
    {
        this.search( HOT_WORD );
    }

    /**
     * Busca por ações
     */
    private void searchForActions()
    {
        this.search( ACTIONS );
    }
}
