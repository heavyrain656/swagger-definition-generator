<?php


namespace tests;

/**
 * Class ClassTestDTO
 * @package tests
 * @property string $virtualProperty
 */
class ClassTestDTO
{
    public const CONSTANT = 1;

    public static $staticProp;
    /**
     * this is a string property
     * @var string
     */
    public $stringProperty;
    /**
     * @var int
     */
    public $intProperty;
    /**
     * @var int[]
     */
    public $typedArrayProperty;
    /**
     * @var array
     */
    public $arrayProperty;
    /**
     * @var mixed
     */
    public $mixedProperty;

    public $undefinedProperty;

    public string $typedProperty;

    private string $privateTypedProperty;
    /**
     * @var string
     */
    protected $protectedTypedProperty;
    /**
     * @var RefTestDTO
     */
    public $objectProperty;

    public RefTestDTO $typedObjectProperty;

    public ?RefTestDTO $typedNullableObjectProperty;

    /**
     * @var RefTestDTO[]
     */
    public array $classArrayProperty;
    /**
     * @var string|int|RefTestDTO
     */
    public $multipleTypesProperty;

    public \Closure $closureProperty;

    public object $unknownObjectProperty;

    public \Throwable $throwableProperty;
}