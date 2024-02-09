package com.bolsadeideas.springboot.reactor.app;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.bolsadeideas.springboot.reactor.app.models.Comentarios;
import com.bolsadeideas.springboot.reactor.app.models.Usuario;
import com.bolsadeideas.springboot.reactor.app.models.UsuarioComentarios;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class SpringBootReactorApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(SpringBootReactorApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringBootReactorApplication.class, args);
	}

	/*
	 * @Override public void run(String... args) throws Exception {//Flux Observable
	 * // TODO Auto-generated method stub Flux<Usuario> nombres =
	 * Flux.just("Brian Guerra", "Rolando Rodriguez", "Luis Covarrubias",
	 * "Pancho Reynoso", "Omar Moreno", "Bruce Lee", "Bruce Willis") //
	 * .doOnNext(elemento -> System.out.println(elemento)); //
	 * .doOnNext(System.out::println); .map(nombre -> new
	 * Usuario(nombre.split(" ")[0].toUpperCase(),nombre.split(" ")[1].toUpperCase()
	 * )) .filter(usuario -> {return
	 * usuario.getNombre().toLowerCase().equals("bruce");}) .doOnNext(usuario -> {
	 * if (usuario == null) { throw new
	 * RuntimeException("Nombres no pueden ser vacios"); } {
	 * System.out.println(usuario.getNombre().concat(" ").concat(usuario.getApellido
	 * ())); } }).map(usuario->{ String nombre = usuario.getNombre().toLowerCase();
	 * usuario.setNombre(nombre); return usuario; });
	 * 
	 * // nombres.subscribe(log::info); nombres.subscribe(e ->
	 * log.info(e.getNombre()), error -> log.error(error.getMessage()), new
	 * Runnable() {
	 * 
	 * @Override public void run() {
	 * log.info("Ha finalizado la ejecucion del observable con exito"); } }); }
	 */

	/*
	 * @Override public void run(String... args) throws Exception {//Flux Observable
	 * inmutable
	 * 
	 * List<String> usuariosList = new ArrayList<>();
	 * usuariosList.add("Brian Guerra"); usuariosList.add("Rolando Rodriguez");
	 * usuariosList.add("Luis Covarrubias"); usuariosList.add("Pancho Reynoso");
	 * usuariosList.add("Omar Moreno"); usuariosList.add("Bruce Lee");
	 * usuariosList.add("Bruce Willis");
	 * 
	 * Flux<String> nombres = Flux.fromIterable(usuariosList); // TODO
	 * Auto-generated method stub //Flux<String> nombres = Flux.just("Brian Guerra",
	 * "Rolando Rodriguez", "Luis Covarrubias", "Pancho Reynoso", "Omar Moreno",
	 * "Bruce Lee", "Bruce Willis"); // .doOnNext(elemento ->
	 * System.out.println(elemento)); // .doOnNext(System.out::println);
	 * Flux<Usuario> usuarios = nombres.map(nombre -> new
	 * Usuario(nombre.split(" ")[0].toUpperCase(),nombre.split(" ")[1].toUpperCase()
	 * )) .filter(usuario -> {return
	 * usuario.getNombre().toLowerCase().equals("bruce");}) .doOnNext(usuario -> {
	 * if (usuario == null) { throw new
	 * RuntimeException("Nombres no pueden ser vacios"); } {
	 * System.out.println(usuario.getNombre().concat(" ").concat(usuario.getApellido
	 * ())); } }).map(usuario->{ String nombre = usuario.getNombre().toLowerCase();
	 * usuario.setNombre(nombre); return usuario; });
	 * 
	 * // nombres.subscribe(log::info); usuarios.subscribe(e ->
	 * log.info(e.getNombre()), error -> log.error(error.getMessage()), new
	 * Runnable() {
	 * 
	 * @Override public void run() {
	 * log.info("Ha finalizado la ejecucion del observable con exito"); } }); }
	 */
	@Override
	public void run(String... args) throws Exception {
		// ejemploIterable();
		// ejemploFlatMap();
		// ejemploToString();
		// ejemploCollectList();
		// ejemploUsuarioComentariosFlatMap();
		// ejemploUsuarioComentariosZipWith();
		// ejemploUsuarioComentariosZipWithForma2();
		// ejemploZipWithRangos();
		// ejemploInterval();
		// ejemploDelayElements();
		// ejemploIntervalInfinito();
		ejemploIntervalDesdeCreate();
	}

	public void ejemploContraPrsion() {
		/*Flux.range(1, 10).log()
				// .subscribe(i -> log.info(i.toString()));
				.subscribe(new Subscriber<Integer>() {
					private Subscription s;
					private Integer limite =2;
					private Integer consumido = 0;

					@Override
					public void onSubscribe(Subscription s) {
						this.s = s;
						s.request(Long.MAX_VALUE);
					}

					@Override
					public void onNext(Integer t) {
						log.info(t.toString());
						consumido++;
						if(consumido == limite) {
							consumido =0;
							s.request(limite);
						}
					}

					@Override
					public void onError(Throwable t) {

					}

					@Override
					public void onComplete() {

					}
				});*/
		Flux.range(1, 10)
		.log()
		.limitRate(2)
		.subscribe();
	}

	public void ejemploIntervalDesdeCreate() {
		Flux.create(emitter -> {
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				private Integer contador = 0;

				@Override
				public void run() {
					emitter.next(++contador);
					if (contador == 10) {
						timer.cancel();
						emitter.complete();
					}
					if (contador == 5) {
						timer.cancel();
						emitter.error(new InterruptedException("Error, se ha detenido el flux en 5!"));
					}
				}
			}, 1000, 1000);
		})
				/*
				 * .doOnNext(next -> log.info(next.toString())) .doOnComplete(()->
				 * log.info("Hemos terminado")) .subscribe();
				 */
				.subscribe(next -> log.info(next.toString()), error -> log.error(error.getMessage()),
						() -> log.info("Hemos terminado"));
	}

	public void ejemploIntervalInfinito() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);

		Flux.interval(Duration.ofSeconds(1))
				// .doOnTerminate(()-> latch.countDown())
				.doOnTerminate(latch::countDown).flatMap(i -> {
					if (i >= 5) {
						return Flux.error(new InterruptedException("Solo hasta 5!"));
					}
					return Flux.just(i);
				}).map(i -> "Hola " + i).retry(2)
				// .doOnNext(s -> log.info(s))
				.subscribe(s -> log.info(s), e -> log.error(e.getMessage()));

		latch.await();
	}

	public void ejemploDelayElements() throws InterruptedException {
		Flux<Integer> rango = Flux.range(1, 12).delayElements(Duration.ofSeconds(1))
				.doOnNext(i -> log.info(i.toString()));
		// rango.subscribe();
		// rango.blockLast();//No es recomendable hacerlo, solo para el ejemplo

		rango.subscribe();
		Thread.sleep(13000);
	}

	public void ejemploInterval() {
		Flux<Integer> rango = Flux.range(1, 12);
		Flux<Long> retraso = Flux.interval(Duration.ofSeconds(1));

		rango.zipWith(retraso, (ra, re) -> ra).doOnNext(i -> log.info(i.toString()))
				// .subscribe();
				.blockLast();// No es recomendable hacerlo, solo para el ejemplo
	}

	public void ejemploZipWithRangos() {
		Flux<Integer> rangos = Flux.range(0, 4);
		Flux.just(1, 2, 3, 4).map(i -> (i * 2))
				// .zipWith(Flux.range(0, 4),(uno,dos) -> String.format("Primer Flux: %d,
				// Segundo Flux:%d",uno,dos))
				.zipWith(rangos, (uno, dos) -> String.format("Primer Flux: %d, Segundo Flux:%d", uno, dos))
				.subscribe(texto -> log.info(texto));
	}

	public void ejemploUsuarioComentariosZipWithForma2() {
		Mono<Usuario> usuarioMono = Mono.fromCallable(() -> new Usuario("John", "Doe"));

		Mono<Comentarios> comentariosUsuarioMono = Mono.fromCallable(() -> {
			Comentarios comentarios = new Comentarios();
			comentarios.addComentario("Hola pepe, que tal!");
			comentarios.addComentario("Mañana voy a la playa");
			comentarios.addComentario("Estoy tomando el curso de spring con reactor");
			return comentarios;
		});

		Mono<UsuarioComentarios> usuarioConComentarios = usuarioMono.zipWith(comentariosUsuarioMono).map(tuple -> {
			Usuario u = tuple.getT1();
			Comentarios c = tuple.getT2();
			return new UsuarioComentarios(u, c);
		});

		usuarioConComentarios.subscribe(uc -> log.info(uc.toString()));
	}

	public void ejemploUsuarioComentariosZipWith() {
		Mono<Usuario> usuarioMono = Mono.fromCallable(() -> new Usuario("John", "Doe"));

		Mono<Comentarios> comentariosUsuarioMono = Mono.fromCallable(() -> {
			Comentarios comentarios = new Comentarios();
			comentarios.addComentario("Hola pepe, que tal!");
			comentarios.addComentario("Mañana voy a la playa");
			comentarios.addComentario("Estoy tomando el curso de spring con reactor");
			return comentarios;
		});

		Mono<UsuarioComentarios> usuarioConComentarios = usuarioMono.zipWith(comentariosUsuarioMono,
				(usuario, comentarioUsuario) -> new UsuarioComentarios(usuario, comentarioUsuario));

		usuarioConComentarios.subscribe(uc -> log.info(uc.toString()));
	}

	public void ejemploUsuarioComentariosFlatMap() {
		Mono<Usuario> usuarioMono = Mono.fromCallable(() -> new Usuario("John", "Doe"));

		Mono<Comentarios> comentariosUsuarioMono = Mono.fromCallable(() -> {
			Comentarios comentarios = new Comentarios();
			comentarios.addComentario("Hola pepe, que tal!");
			comentarios.addComentario("Mañana voy a la playa");
			comentarios.addComentario("Estoy tomando el curso de spring con reactor");
			return comentarios;
		});

		usuarioMono.flatMap(u -> comentariosUsuarioMono.map(c -> new UsuarioComentarios(u, c)))
				.subscribe(uc -> log.info(uc.toString()));
	}

	public void ejemploCollectList() throws Exception {

		List<Usuario> usuariosList = new ArrayList<>();
		usuariosList.add(new Usuario("Brian", "Guerra"));
		usuariosList.add(new Usuario("Rolando", "Rodriguez"));
		usuariosList.add(new Usuario("Luis", "Covarrubias"));
		usuariosList.add(new Usuario("Pancho", "Reynoso"));
		usuariosList.add(new Usuario("Omar", "Moreno"));
		usuariosList.add(new Usuario("Bruce", "Lee"));
		usuariosList.add(new Usuario("Bruce", "Willis"));

		Flux.fromIterable(usuariosList).collectList().subscribe(lista -> {
			lista.forEach(item -> log.info(lista.toString()));
		});
	}

	public void ejemploToString() throws Exception {

		List<Usuario> usuariosList = new ArrayList<>();
		usuariosList.add(new Usuario("Brian", "Guerra"));
		usuariosList.add(new Usuario("Rolando", "Rodriguez"));
		usuariosList.add(new Usuario("Luis", "Covarrubias"));
		usuariosList.add(new Usuario("Pancho", "Reynoso"));
		usuariosList.add(new Usuario("Omar", "Moreno"));
		usuariosList.add(new Usuario("Bruce", "Lee"));
		usuariosList.add(new Usuario("Bruce", "Willis"));

		Flux.fromIterable(usuariosList).map(
				usuario -> usuario.getNombre().toUpperCase().concat(" ").concat(usuario.getApellido().toUpperCase()))
				.flatMap(nombre -> {
					if (nombre.contains("bruce".toUpperCase())) {
						return Mono.just(nombre);
					} else {
						return Mono.empty();
					}
				}).map(nombre -> {
					return nombre.toLowerCase();
				}).subscribe(u -> log.info(u.toString()));
	}

	public void ejemploFlatMap() throws Exception {

		List<String> usuariosList = new ArrayList<>();
		usuariosList.add("Brian Guerra");
		usuariosList.add("Rolando Rodriguez");
		usuariosList.add("Luis Covarrubias");
		usuariosList.add("Pancho Reynoso");
		usuariosList.add("Omar Moreno");
		usuariosList.add("Bruce Lee");
		usuariosList.add("Bruce Willis");

		Flux.fromIterable(usuariosList)
				.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(), nombre.split(" ")[1].toUpperCase()))
				.flatMap(usuario -> {
					if (usuario.getNombre().equalsIgnoreCase("bruce")) {
						return Mono.just(usuario);
					} else {
						return Mono.empty();
					}
				}).map(usuario -> {
					String nombre = usuario.getNombre().toLowerCase();
					usuario.setNombre(nombre);
					return usuario;
				}).subscribe(u -> log.info(u.toString()));
	}

	public void ejemploIterable() throws Exception {

		List<String> usuariosList = new ArrayList<>();
		usuariosList.add("Brian Guerra");
		usuariosList.add("Rolando Rodriguez");
		usuariosList.add("Luis Covarrubias");
		usuariosList.add("Pancho Reynoso");
		usuariosList.add("Omar Moreno");
		usuariosList.add("Bruce Lee");
		usuariosList.add("Bruce Willis");

		Flux<String> nombres = Flux.fromIterable(usuariosList);

		Flux<Usuario> usuarios = nombres
				.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(), nombre.split(" ")[1].toUpperCase()))
				.filter(usuario -> {
					return usuario.getNombre().toLowerCase().equals("bruce");
				}).doOnNext(usuario -> {
					if (usuario == null) {
						throw new RuntimeException("Nombres no pueden ser vacios");
					}
					{
						System.out.println(usuario.getNombre().concat(" ").concat(usuario.getApellido()));
					}
				}).map(usuario -> {
					String nombre = usuario.getNombre().toLowerCase();
					usuario.setNombre(nombre);
					return usuario;
				});

		usuarios.subscribe(e -> log.info(e.getNombre()), error -> log.error(error.getMessage()), new Runnable() {
			@Override
			public void run() {
				log.info("Ha finalizado la ejecucion del observable con exito");
			}
		});
	}

}
