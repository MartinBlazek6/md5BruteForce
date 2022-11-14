package com.md5cracker.md5cracker;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.MessageDigest;

@RestController
public class Controller {
    MessageDigest md;

    char min_char_value;
    char max_char_value;

    char[] guess;

    int max_num_chars;


    public Controller() throws Exception
    {
        min_char_value = 32;
        max_char_value = 126;
        max_num_chars = 10;

        md = MessageDigest.getInstance("MD5");

        guess = null;
    }

    public String crack(String hash)
    {
        boolean done = false;
        String guess_hash;

        for(int num_chars = 0; num_chars < max_num_chars && !done; num_chars++)
        {
            // Initialize guess at the start of each interation
            guess = new char[num_chars];
            for(int x = 0; x < num_chars; x++)
            {
                guess[x] = min_char_value;
            }

            while(canIncrementGuess() && !done)
            {
                incrementGuess();
                md.reset();
                md.update(new String(guess).getBytes());
                guess_hash = hashToString(md.digest());

                if(hash.equals(guess_hash))
                {
                    done = true;
                }
            }
        }
        return new String(guess);
    }

    protected boolean canIncrementGuess()
    {
        boolean canIncrement = false;

        for (char c : guess) {
            if (c < max_char_value) {
                canIncrement = true;
                break;
            }
        }
        return canIncrement;
    }

    protected void incrementGuess()
    {
        boolean incremented = false;

        for(int x = (guess.length - 1);x >= 0 && !incremented; x--)
        {
            if(guess[x] < max_char_value)
            {
                guess[x]++;
                if(x < (guess.length-1))
                {
                    guess[x+1] = min_char_value;
                }
                incremented = true;
            }
        }
    }

    protected String hashToString(byte[] hash)
    {
        StringBuilder sb = new StringBuilder();

        for (byte b : hash) {
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
    @GetMapping("/crack")
    ResponseEntity<String> cracker(@RequestParam String id) {

        System.out.println("lala");
        HttpHeaders headers = null;
        if (id.length() > 0) {
            try {
                Controller bc = new Controller();
                long start;
                long end;
                String answer;

                start = System.nanoTime();
                answer = bc.crack(id);
                end = System.nanoTime();

                System.out.println("Answer: " + answer);
                System.out.println("Processing Time: " + ((end - start) / 1000000000));
                headers = new HttpHeaders();
                headers.add("pass", answer);
            } catch (Exception e) {
                System.out.println("Exception: " + e.toString());
            }
        }

        return new ResponseEntity<>(
                "Custom header set", headers, HttpStatus.OK);
    }



}
